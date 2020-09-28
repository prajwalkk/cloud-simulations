package com.prajwalkk.cloudsim.utils.ServiceUtilities

import com.prajwalkk.cloudsim.utils.ProviderModels.{CloudletModel, IaaSModel, VMModel}
import com.prajwalkk.cloudsim.utils.ServiceModels.{IaasServiceModel, PaasServiceModel, SaasServiceModel}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, NetworkCloudlet}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.autoscaling.{HorizontalVmScaling, HorizontalVmScalingSimple}

import scala.util.Random

/*
*
* Created by: prajw
* Date: 26-Sep-20
*
*/
/**
 * Class that can be instanciated to create VMs, Cloudlets
 *
 * @param paasServiceModel Service config of paas
 * @param iaasServiceModel Service config of iaas
 * @param saasServiceModel Service config of saas
 */
case class ServiceUtils(val paasServiceModel: PaasServiceModel,
                        val iaasServiceModel: IaasServiceModel,
                        val saasServiceModel: SaasServiceModel) extends LazyLogging {

  // def uuid = java.util.UUID.randomUUID.toString

  /**
   * Creates a list of VMs with specified config
   *
   * @param number  number of vms
   * @param vmModel This is to differenciate Iaas, Paas, SaaS Vms
   * @param model   same as above
   * @return [[Vm]] List
   */
  def createVMList(number: Int = 0, vmModel: VMModel = null, model: String = ""): List[Vm] =
    model match {
      // in case of Iaas, the VMs are chosen from the user config
      case "IaaS" => (1 to iaasServiceModel.VM_NUM).map(_ => createVm()).toList
      // incase of Saas the vms and thier numbers are chosen from the datacenter
      case "SaaS" if (number != 0 && vmModel != null) => (1 to number).map(_ => createVm(vmModel)).toList
      // in case if Paas the number of Vms are chosen from user. But the vm sizes are not
      case "PaaS" if (vmModel != null) => (1 to paasServiceModel.VM_NUMBER).map(_ => createVm(vmModel)).toList
      case _ => throw new RuntimeException("No Model/number specified for Saas/Paas")
    }

  /**
   * Create a single Vm
   * @return [[Vm]]
   */
  def createVm(): Vm = {
    new VmSimple(iaasServiceModel.VM_MIPS, iaasServiceModel.VM_PES)
      .setRam(iaasServiceModel.VM_RAM)
      .setBw(iaasServiceModel.VM_BW)
      .setSize(iaasServiceModel.VM_SIZE)
  }

  /**
   * Create Vm for Paas, Iaas
   * @param vmModel
   * @return
   */
  def createVm(vmModel: VMModel): Vm = {
    new VmSimple(vmModel.vmMips, vmModel.getVmPes)
      .setRam(vmModel.vmRam)
      .setBw(vmModel.vmBw)
      .setSize(vmModel.vmSize)
  }

  /**
   * Cloudlets creation function for different models
   * @param cloudletModel
   * @param model String for Iaas, Paas, Saas
   * @return
   */
  def createCloudlets(cloudletModel: CloudletModel = null, model: String): List[NetworkCloudlet] =
    model match {
      case "SaaS" if (cloudletModel != null) => (1 to saasServiceModel.CLOUDLET_NUMBER).map(id => createCloudlet(id, cloudletModel)).toList
      case "PaaS" => (1 to paasServiceModel.CLOUDLET_NUMBER).map(id => createCloudlet(id, model)).toList
      case "IaaS" => (1 to iaasServiceModel.CLOUDLET_NUMBER).map(id => createCloudlet(id, model)).toList
      case _ => throw new Exception("Wrong model passed while creating cloudlets. SaaS/PaaS/IaaS")
    }

  /**
   * Create a single cloudlet for a specific model
   * @param id
   * @param model
   * @return
   */
  def createCloudlet(id: Int, model: String): NetworkCloudlet = {
    model match {
      case "PaaS" => {
        val c = new NetworkCloudlet(id + saasServiceModel.CLOUDLET_NUMBER, getRandomLength(paasServiceModel.CLOUDLET_MIN_LEN, paasServiceModel.CLOUD_MAX_LEN), paasServiceModel.CLOUDLET_PES)
          .setFileSize(paasServiceModel.CLOUDLET_FILE_SIZE)
          .setUtilizationModel(new UtilizationModelFull())
          .setOutputSize(paasServiceModel.CLOUDLET_OUTPUT_SIZE).asInstanceOf[NetworkCloudlet]
        c.addTask(new CloudletExecutionTask(id, getRandomLength(paasServiceModel.CLOUDLET_MIN_LEN, paasServiceModel.CLOUD_MAX_LEN)))
      }
      case "IaaS" => {
        val c = new NetworkCloudlet(id + saasServiceModel.CLOUDLET_NUMBER + paasServiceModel.CLOUDLET_NUMBER, getRandomLength(iaasServiceModel.CLOUDLET_MIN_LEN, iaasServiceModel.CLOUD_MAX_LEN), iaasServiceModel.CLOUDLET_PES)
          .setFileSize(iaasServiceModel.CLOUDLET_FILE_SIZE)
          .setUtilizationModel(new UtilizationModelFull())
          .setOutputSize(iaasServiceModel.CLOUDLET_OUTPUT_SIZE).asInstanceOf[NetworkCloudlet]
        c.addTask(new CloudletExecutionTask(id, getRandomLength(paasServiceModel.CLOUDLET_MIN_LEN, paasServiceModel.CLOUD_MAX_LEN)))
      }
    }

  }

  /**
   * create a specific cloudlet when a pojo class [[CloudletModel]] is passed
   * @param id
   * @param cloudletModel
   * @return
   */
  def createCloudlet(id: Int, cloudletModel: CloudletModel): NetworkCloudlet = {
    val c = new NetworkCloudlet(id, getRandomLength(cloudletModel.minLength, cloudletModel.maxLength), cloudletModel.cloudletPes)
      .setFileSize(cloudletModel.fileSize)
      .setUtilizationModel(new UtilizationModelFull())
      .setOutputSize(cloudletModel.outputSize).asInstanceOf[NetworkCloudlet]
    c.addTask(new CloudletExecutionTask(id, getRandomLength(paasServiceModel.CLOUDLET_MIN_LEN, paasServiceModel.CLOUD_MAX_LEN)))
  }

  /**
   * This is used to implement random length for cloudlets
   * @param start
   * @param end
   * @return
   */
  def getRandomLength(start: Long, end: Long): Long = {
    val rnd = new Random
    start + rnd.nextLong((end - start) + 1)
  }

  /**
   * Create list of scalable Vms
   * @param vmModel
   * @return
   */
  def createInitialScalableVms(vmModel: VMModel): List[Vm] =
    (1 to vmModel.vmNumber).map { _ =>
      val vm: Vm = createVm(vmModel)
      createHorizontalVmScaling(vm, vmModel)

      //gather the VM
      vm
    }.toList

  /**
   * Create horizontal scaling Vms when Cpu goes beyond 70%
   * @param vm
   * @param vmModel
   */
  def createHorizontalVmScaling(vm: Vm, vmModel: VMModel): Unit = {
    val horizontalVmScaling: HorizontalVmScaling = new HorizontalVmScalingSimple()
    horizontalVmScaling
      .setVmSupplier(() => createVm(vmModel))
      .setOverloadPredicate(isVmOverloaded)
    vm.setHorizontalScaling(horizontalVmScaling)
  }

  /**
   * Checks if cpu is overloaded
   * @param vm
   * @return
   */
  def isVmOverloaded(vm: Vm): Boolean = {
    vm.getCpuPercentUtilization > 0.7
  }

  /**
   * Adds execution task for network cloudlets
   * @param networkCloudlet
   * @param model
   * @return
   */
  def addExecutionTask(networkCloudlet: NetworkCloudlet, model: String) = {
    logger.info(s"Adding executionTask $networkCloudlet, $model")
    val task: CloudletExecutionTask = new CloudletExecutionTask(
      networkCloudlet.getTasks.size(), networkCloudlet.getLength)
    model match {
      case "IaaS" =>
        task.setMemory(iaasServiceModel.CLOUDLET_RAM)
        networkCloudlet.addTask(task)
      case "PaaS" =>
        task.setMemory(paasServiceModel.CLOUDLET_RAM)
        networkCloudlet.addTask(task)
      case "SaaS" =>
        task.setMemory(saasServiceModel.CLOUDLET_RAM)
        networkCloudlet.addTask(task)
    }

  }

}
