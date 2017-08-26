package me.shadaj.slinky

import me.shadaj.slinky.core.BaseComponentWrapper
import me.shadaj.slinky.core.facade.React

import scala.scalajs.js

package object hot {
  def initialize(): Unit = {
    if (js.isUndefined(js.Dynamic.global.proxies)) {
      js.Dynamic.global.proxies = js.Dynamic.literal()
    }

    BaseComponentWrapper.insertMiddleware((constructor, component) => {
      if (js.isUndefined(component.asInstanceOf[js.Dynamic]._hot)) {
        component.asInstanceOf[js.Dynamic]._hot = true

        if (js.isUndefined(js.Dynamic.global.proxies.selectDynamic(this.getClass.getName))) {
          println("creating proxy")
          js.Dynamic.global.proxies.updateDynamic(this.getClass.getName)(ReactProxy.createProxy(constructor))
        } else {
          println("updating proxy")
          val forceUpdate = ReactProxy.getForceUpdate(React)
          js.Dynamic.global.proxies.selectDynamic(this.getClass.getName)
            .update(constructor).asInstanceOf[js.Array[js.Object]]
            .foreach(o => forceUpdate(o))
        }
      }

      js.Dynamic.global.proxies.selectDynamic(this.getClass.getName).get().asInstanceOf[js.Object]
    })
  }
}
