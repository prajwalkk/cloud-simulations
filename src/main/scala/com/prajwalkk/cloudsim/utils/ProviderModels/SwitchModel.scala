package com.prajwalkk.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty


/*
*
* Created by: prajw
* Date: 21-Sep-20
*
*/
class SwitchModel(@BeanProperty var number: Int,
                  @BeanProperty var numPorts: Int,
                  @BeanProperty var bw: Long,
                  @BeanProperty var switchingDelay: Long) {


  def this() = this(0, 0, 0, 0)

}
