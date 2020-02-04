package slinky

import slinky.core.BaseComponentWrapper
import slinky.core.facade.ReactRaw

import scala.scalajs.js

package object hot {
  def initialize(): Unit = {
    if (js.isUndefined(js.Dynamic.global.proxies)) {
      js.Dynamic.global.proxies = js.Dynamic.literal()
    }

    BaseComponentWrapper.insertMiddleware { (constructor, component) =>
      val componentName = component.asInstanceOf[BaseComponentWrapper].getClass.getName

      if (js.isUndefined(component.asInstanceOf[js.Dynamic]._hot)) {
        component.asInstanceOf[js.Dynamic]._hot = true

        if (js.isUndefined(js.Dynamic.global.proxies.selectDynamic(componentName))) {
          js.Dynamic.global.proxies.updateDynamic(componentName)(ReactProxy.createProxy(constructor))
        } else {
          val forceUpdate = ReactProxy.getForceUpdate(ReactRaw)
          js.Dynamic.global.proxies
            .selectDynamic(componentName)
            .update(constructor)
            .asInstanceOf[js.Array[js.Object]]
            .foreach(o => forceUpdate(o))
        }
      }

      js.Dynamic.global.proxies.selectDynamic(componentName).get().asInstanceOf[js.Object]
    }

    BaseComponentWrapper.enableScalaComponentWriting()
  }
}
