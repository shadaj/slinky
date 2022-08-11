package slinky.web

import org.scalajs.dom.Element

@js.native
@JSImport("react-dom", JSImport.Namespace, "ReactDOM")
object ReactDOM extends js.Object {
  def render(component: ReactElement, target: Element): ReactInstance  = js.native
  def hydrate(component: ReactElement, target: Element): ReactInstance = js.native
  def findDOMNode(instance: React.Component): Element                  = js.native

  def unmountComponentAtNode(container: Element): Unit = js.native

  /**
    * React Docs - Creates a portal. Portals provide a way to render children into a DOM node that exists outside the hierarchy of the DOM component.
    *
    * React 16 only
    * @param child the React node to render inside the selected container
    * @param container the DOM node to render the child node inside
    * @return a portal React element
    */
  def createPortal(child: ReactElement, container: Element): ReactElement = js.native
}

@js.native
@JSImport("react-dom/server", JSImport.Namespace, "ReactDOMServer")
object ReactDOMServer extends js.Object {
  def renderToString(element: ReactElement): String       = js.native
  def renderToStaticMarkup(element: ReactElement): String = js.native

  def renderToNodeStream(element: ReactElement): js.Object       = js.native
  def renderToStaticNodeStream(element: ReactElement): js.Object = js.native
}

trait ReactRoot extends js.Object {
  def render(component: ReactElement): ReactInstance
}

@js.native
@JSImport("react-dom/client", JSImport.Namespace, "ReactDOM")
object ReactDOMClient extends js.Object {
  def createRoot(target: Element): ReactRoot = js.native
}