package com.prajwalkk.cloudsim.utils.ProviderModels

import java.beans.BeanProperty

/*
*
* Created by: prajw
* Date: 25-Sep-20
*
*/
case class CloudletModel(@BeanProperty var number: Int,
                         @BeanProperty var cloudletPes: Int,
                         @BeanProperty var minLength: Long,
                         @BeanProperty var maxLength: Long,
                         @BeanProperty var fileSize: Long,
                         @BeanProperty var outputSize: Long) {


  def this() = this(0, 0, 0, 0, 0, 0)

}
