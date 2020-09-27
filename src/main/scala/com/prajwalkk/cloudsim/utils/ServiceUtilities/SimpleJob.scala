package com.prajwalkk.cloudsim.utils.ServiceUtilities

import com.prajwalkk.cloudsim.ExampleSim.{cloudlet_length, cloudlet_pes, vm_pes}
import com.prajwalkk.cloudsim.utils.ProviderModels.{CloudletModel, VMModel}
import com.prajwalkk.cloudsim.utils.ServiceModels.{IaasServiceModel, PaasServiceModel}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModelDynamic, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}

/*
*
* Created by: prajw
* Date: 20-Sep-20
*
*/
class SimpleJob(var simulation: Simulation,
                var broker: DatacenterBroker,
                val serviceModel: IaasServiceModel) {


  def createVMList: List[Vm] =
    (1 to serviceModel.VM_NUM).map(_ => new VmSimple(serviceModel.VM_MIPS, serviceModel.VM_PES)
      .setRam(serviceModel.VM_RAM)
      .setBw(serviceModel.VM_BW)
      .setSize(serviceModel.VM_SIZE)).toList



  def createCloudletSimpleList: List[Cloudlet] = {
    val utilizationModel: UtilizationModelFull = new UtilizationModelFull()
    (1 to serviceModel.CLOUDLET_NUMBER).map { _ =>
      new CloudletSimple(serviceModel.CLOUD_MAX_LEN, serviceModel.CLOUDLET_PES, utilizationModel)
    }.toList
  }


  // NEW Func




}
