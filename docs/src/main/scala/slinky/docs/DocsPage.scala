package slinky.docs

import slinky.core.{FunctionalComponent, ReactComponentClass}
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.remarkreact.{ReactRenderer, Remark}
import slinky.web.html._
import slinky.next.Head
import slinky.next.Router

import scala.scalajs.js
import js.Dynamic.literal
import scala.concurrent.Future
import scala.scalajs.js.JSConverters._
import js.annotation.JSImport

@react object RemarkCode {
  case class Props(children: Seq[String])

  val component = FunctionalComponent[Props] { props =>
    if (props.children.head.contains('\n')) {
      div(className := "code-block", style := literal(
        borderRadius = "10px",
        overflow = "hidden"
      ))(
        SyntaxHighlighter(language = "scala", style = prismColors)(
          props.children.head
        )
      )
    } else {
      code(props.children.head)
    }
  }

  // from the reactjs.org theme
  val prismColors = js.Dictionary[js.Object](
    "hljs-comment" -> literal(color = "#999999"),
    "hljs-keyword" -> literal(color = "#c5a5c5"),
    "hljs-built_in" -> literal(color = "#5a9bcf"),
    "hljs-string" -> literal(color = "#8dc891"),
    "hljs-variable" -> literal(color = "#d7deea"),
    "hljs-title" -> literal(color = "#79b6f2"),
    "hljs-type" -> literal(color = "#FAC863"),
    "hljs-meta" -> literal(color = "#FAC863"),
    "hljs-strong" -> literal(fontWeight = 700),
    "hljs-emphasis" -> literal(fontStyle = "italic"),
    "hljs" -> literal(
      backgroundColor = "#282c34",
      color = "#ffffff",
      fontSize = "15px",
      lineHeight = "20px",
      padding = "20px",
      overflow = "auto",
      marginBottom = "0px"
    ),
    "code[class*=\"language-\"]" -> literal(
      backgroundColor = "#282c34",
      color = "#ffffff"
    )
  )
}

@react object RemarkH1 {
  case class Props(children: Seq[ReactElement])

  val component = FunctionalComponent[Props] { props =>
    Fragment(
      props.children.headOption.map { head =>
        Head(
          title(s"$head | Slinky - Write React apps in Scala just like ES6")
        )
      },
      h1(props.children: _*)
    )
  }
}

@react object RemarkH2 {
  case class Props(children: Seq[String])

  val component = FunctionalComponent[Props] { props =>
    Fragment(
      hr(style := literal(
        height = "1px",
        marginBottom = "-1px",
        border = "none",
        borderBottom = "1px solid #ececec",
        marginTop = "40px"
      )),
      h2(props.children.head)
    )
  }
}

object DocsTree {
  val tree: List[(String, List[(String, String)])] = List(
    "Core Concepts" -> List(
      "Installation" -> "installation",
      "Hello World!" -> "hello-world",
      "Why Slinky?" -> "why-slinky",
      "The Tag API" -> "the-tag-api",
      "Writing Components" -> "writing-components",
      "External Components" -> "external-components",
      "Functional Components and Hooks" -> "functional-components-and-hooks",
      "React Native and VR" -> "native-and-vr",
      "Electron" -> "electron",
    ),
    "Advanced Guides" -> List(
      "Technical Overview" -> "technical-overview",
      "Custom Tags and Attributes" -> "custom-tags-and-attributes",
      "Fragments and Portals" -> "fragments-and-portals",
      "Context" -> "context",
      "Refs" -> "refs",
      "Error Boundaries" -> "error-boundaries",
      "Exporting Components" -> "exporting-components",
      "Scala.js React Interop" -> "scalajs-react-interop",
      "Abstracting Over Tags" -> "abstracting-over-tags",
    ),"" -> List(
      "Resources" -> "resources",
    )
  )
}

object TrackSSRDocs {
  var publicSSR: js.Dictionary[String] = js.Dictionary.empty[String]

  def getPublic(page: String): String = {
    val pageLocation = "../../../../public" + page
    val ret = js.Dynamic.global.fs.readFileSync(pageLocation, "UTF-8").asInstanceOf[String]
    publicSSR(page) = ret
    ret
  }
}

@react object DocsPage {
  def docsFilePath(query: js.Dynamic) = {
    val matchString = query.id.toString//props.selectDynamic("match").params.selectDynamic("0").toString
    s"/docs/${matchString.reverse.dropWhile(_ == '/').reverse}.md"
  }

  case class Props(document: String)

  val component = FunctionalComponent[Props] { props =>
    val query = Router.useRouter().query
    val matchString = query.id.toString

    val document = props.document

    DocsTree.tree.find(_._2.exists(_._2 == matchString)).map { case (selectedGroup, _) =>
      div(className := "article fill-right", style := literal(
        marginTop = "40px",
        paddingLeft = "15px",
        boxSizing = "border-box"
      ))(
        div(style := literal(
          display = "flex",
          flexDirection = "row"
        ), className := "docs-page")(
          div(style := literal(
            width = "calc(100% - 300px)"
          ), className := "docs-content")(
            div(style := literal(maxWidth = "1400px"))(
              Remark().use(ReactRenderer, literal(
                remarkReactComponents = literal(
                  h1 = RemarkH1.component: ReactComponentClass[_],
                  h2 = RemarkH2.component: ReactComponentClass[_],
                  code = RemarkCode.component: ReactComponentClass[_]
                )
              )).processSync(document).result
            )
          ),
          div(style := literal(
            width = "300px",
            marginLeft = "20px"
          ), className := "docs-sidebar")(
            div(
              style := literal(
                position = "fixed",
                top = "60px",
                height = "calc(100vh - 60px)",
                backgroundColor = "#f7f7f7",
                borderLeft = "1px solid #ececec",
                paddingTop = "40px",
                paddingRight = "1000px",
                boxSizing = "border-box"
              ),
              className := "docs-sidebar-content"
            )(
              nav(style := literal(
                position = "relative",
                paddingLeft = "20px",
                width = "300px"
              ))(
                DocsTree.tree.map { case (group, value) =>
                  DocsGroup(
                    name = group,
                    curId = matchString,
                    isOpen = group == selectedGroup
                  )(value).withKey(group)
                }
              )
            )
          )
        )
      )
    }
  }

  object Next {
    import slinky.core.ReactComponentClass
    import scala.scalajs.js.annotation.JSExportTopLevel

    @JSExportTopLevel(name = "default", moduleID = "docs-id")
    val component: ReactComponentClass[_] = DocsPage.component
  }

  object NextServer {
    import scala.scalajs.js.annotation.JSExportTopLevel

    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    @JSExportTopLevel(name = "getStaticPaths", moduleID = "docs-id-server")
    def getStaticPaths(): js.Promise[js.Object] = {
      Future(
        js.Dynamic.literal(
          paths = DocsTree.tree.flatMap { case (group, value) =>
            value.map { case (name, id) =>
              js.Dynamic.literal(
                params = js.Dynamic.literal(
                  id = id
                )
              )
            }
          }.toJSArray,
          fallback = false
        )
      ).toJSPromise
    }

    @JSExportTopLevel(name = "getStaticProps", moduleID = "docs-id-server")
    def getStaticProps(props: js.Dynamic): js.Promise[js.Object] = {
      fs.promises.readFile(s"public/docs/${props.params.id}.md", "UTF-8").toFuture.map { t =>
        js.Dynamic.literal(
          props = js.Dynamic.literal(
            document = t
          )
        )
      }.toJSPromise
    }
  }
}

@js.native
@JSImport("fs", JSImport.Namespace)
object fs extends js.Object {
  @js.native
  object promises extends js.Object {
    def readFile(path: String, encoding: String): js.Promise[String] = js.native
  }
}
