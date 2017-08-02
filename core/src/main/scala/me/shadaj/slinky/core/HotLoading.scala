package me.shadaj.slinky.core

import me.shadaj.slinky.core.facade.{React, ReactProxy}

import scala.scalajs.{LinkingInfo, js}
import scala.scalajs.js.ConstructorTag

trait HotLoading extends Component {
  final val enableHotLoading = ReactProxy != js.undefined && LinkingInfo.developmentMode
  private var processedEnable = false

  override def componentConstructor(implicit constructorTag: ConstructorTag[Def]): js.Object = {
    if (enableHotLoading) {
      if (!processedEnable) {
        processedEnable = true

        if (enableHotLoading) {
          if (js.Dynamic.global.proxies == js.undefined) {
            js.Dynamic.global.proxies = js.Dynamic.literal()
          }

          if (js.Dynamic.global.proxies.selectDynamic(this.getClass.getName) == js.undefined) {
            println("creating proxy")
            js.Dynamic.global.proxies.updateDynamic(this.getClass.getName)(ReactProxy.createProxy(super.componentConstructor))
          } else {
            println("updating proxy")
            val forceUpdate = ReactProxy.getForceUpdate(React)
            js.Dynamic.global.proxies.selectDynamic(this.getClass.getName).update(super.componentConstructor).asInstanceOf[js.Array[js.Object]]
              .foreach(o => forceUpdate(o))
          }
        }
      }

      js.Dynamic.global.proxies.selectDynamic(this.getClass.getName).get().asInstanceOf[js.Object]
    } else {
      super.componentConstructor
    }
  }
}
