package com.prajwalkk.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty


/*
*
* Created by: prajw
* Date: 15-Sep-20
*
*/

/**
 * Configuration of a host in dataCenter.
 *
 * referred from
 * https://www.bigbeeconsultants.uk/post/2010/easy-pojos-scala/
 * https://stackoverflow.com/questions/38615086/case-class-instantiation-from-typesafe-config
 *
 * */
case class HostModel(@BeanProperty var number: Int,
                     @BeanProperty var ram: Long,
                     @BeanProperty var storage: Long,
                     @BeanProperty var bw: Long,
                     @BeanProperty var mips: Double,
                     @BeanProperty var cores: Int,
                     @BeanProperty var vmScheduler: String) {
  def this() = this(0, 0, 0, 0, 0, 0, "")
}
