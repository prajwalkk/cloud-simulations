package com.prajwalkk.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty

/*
*
* Created by: prajw
* Date: 24-Sep-20
*
*/
case class VMModel(@BeanProperty var vmNumber: Int,
                   @BeanProperty var vmMips: Double,
                   @BeanProperty var vmSize: Int,
                   @BeanProperty var vmRam: Int,
                   @BeanProperty var vmBw: Int,
                   @BeanProperty var vmPes: Int,
                   @BeanProperty var VmVmm: String) {

  def this() = this(0,0,0,0,0,0,"")

}
