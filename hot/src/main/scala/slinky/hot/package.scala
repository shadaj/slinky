package slinky

import slinky.core.BaseComponentWrapper
import slinky.core.facade.ReactRaw

import scala.scalajs.js

package object hot {
  def initialize(): Unit = {
    val dynamicReactProxyModule = ReactProxy.asInstanceOf[js.Dynamic]
    val proxyObject: js.Dynamic =
      if (js.isUndefined(dynamicReactProxyModule._proxies)) {
        val newProxyStore = js.Dynamic.literal()
        dynamicReactProxyModule._proxies = newProxyStore
        newProxyStore
      } else {
        dynamicReactProxyModule._proxies
      }

    BaseComponentWrapper.insertMiddleware { (constructor, component) =>
      val componentName = component.asInstanceOf[BaseComponentWrapper].getClass.getName

      if (js.isUndefined(component.asInstanceOf[js.Dynamic]._hot)) {
        component.asInstanceOf[js.Dynamic]._hot = true

        if (js.isUndefined(proxyObject.selectDynamic(componentName))) {
          proxyObject.updateDynamic(componentName)(ReactProxy.createProxy(constructor))
        } else {
          val forceUpdate = ReactProxy.getForceUpdate(ReactRaw)
          proxyObject
            .selectDynamic(componentName)
            .update(constructor)
            .asInstanceOf[js.Array[js.Object]]
            .foreach(o => forceUpdate(o))
        }
      }

      proxyObject.selectDynamic(componentName).get().asInstanceOf[js.Object]
    }

    BaseComponentWrapper.enableScalaComponentWriting()
  }
}
