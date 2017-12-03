package me.shadaj.slinky.reactrouter

import me.shadaj.slinky.core.{ExternalComponent, ExternalComponentWithAttributes, NoExternalProps, ReactComponentClass}
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html.a

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-router", JSImport.Default)
@js.native
object ReactRouter extends js.Object {
  val StaticRouter: js.Object = js.native
}

@JSImport("react-router-dom", JSImport.Default)
@js.native
object ReactRouterDOM extends js.Object {
  val BrowserRouter: js.Object = js.native
  val Route: js.Object = js.native
  val Switch : js.Object = js.native
  val Link: js.Object = js.native
}

@react object StaticRouter extends ExternalComponent {
  case class Props(location: String, context: js.Object)

  override val component = ReactRouter.StaticRouter
}

@react object BrowserRouter extends ExternalComponent {
  type Props = NoExternalProps

  override val component = ReactRouterDOM.BrowserRouter
}

@react object Switch extends ExternalComponent {
  type Props = NoExternalProps

  override val component = ReactRouterDOM.Switch
}

@react object Route extends ExternalComponent {
  case class Props(path: String, component: ReactComponentClass, exact: Boolean = false)
  override val component = ReactRouterDOM.Route
}

@react object Link extends ExternalComponentWithAttributes[a.tag.type] {
  case class Props(to: String)
  override val component = ReactRouterDOM.Link
}
