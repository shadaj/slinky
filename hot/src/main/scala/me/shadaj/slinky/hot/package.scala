package me.shadaj.slinky

import me.shadaj.slinky.core.BaseComponentWrapper

import scala.scalajs.js

package object hot {
  def initialize(): Unit = {
    if (js.isUndefined(js.Dynamic.global.previousStates)) {
      js.Dynamic.global.previousStates = js.Dynamic.literal()
    }

    BaseComponentWrapper.insertGetInitialWrittenStateMiddleware { name =>
      if (js.isUndefined(js.Dynamic.global.previousStates.selectDynamic(name))) {
        None
      } else {
        Some(js.Dynamic.global.previousStates.selectDynamic(name).apply().asInstanceOf[js.Object])
      }
    }

    BaseComponentWrapper.insertWrittenStateMiddleware { (name, getter) =>
      js.Dynamic.global.previousStates.updateDynamic(name)(getter)
    }
  }
}
