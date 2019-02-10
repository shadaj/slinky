package slinky.docs

import slinky.core.{Component, StatelessComponent, FunctionalComponent, ReactComponentClass}
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.facade.Hooks._
import slinky.remarkreact.{ReactRenderer, Remark}
import slinky.web.html._
import org.scalajs.dom
import org.scalajs.dom.raw.XMLHttpRequest
import slinky.reacthelmet.Helmet

import scala.scalajs.js
import js.Dynamic.literal

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
        Helmet(
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
      "Installation" -> "/docs/installation/",
      "Hello World!" -> "/docs/hello-world/",
      "Why Slinky?" -> "/docs/why-slinky/",
      "The Tag API" -> "/docs/the-tag-api/",
      "Writing Components" -> "/docs/writing-components/",
      "External Components" -> "/docs/external-components/",
      "Functional Components" -> "/docs/functional-components/",
      "React Native and VR" -> "/docs/native-and-vr/"
    ),
    "Advanced Guides" -> List(
      "Technical Overview" -> "/docs/technical-overview/",
      "Custom Tags and Attributes" -> "/docs/custom-tags-and-attributes/",
      "Fragments and Portals" -> "/docs/fragments-and-portals/",
      "Context" -> "/docs/context/",
      "Refs" -> "/docs/refs/",
      "Error Boundaries" -> "/docs/error-boundaries/",
      "Exporting Components" -> "/docs/exporting-components/",
      "Scala.js React Interop" -> "/docs/scalajs-react-interop/",
      "Abstracting Over Tags" -> "/docs/abstracting-over-tags/",
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
  def docsFilePath(props: js.Dynamic) = {
    val matchString = props.selectDynamic("match").params.selectDynamic("0").toString
    s"/docs/${matchString.reverse.dropWhile(_ == '/').reverse}.md"
  }

  val component = FunctionalComponent[js.Dynamic] { props =>
    val matchString = props.selectDynamic("match").params.selectDynamic("0").toString
    val selectedGroup = DocsTree.tree.find(_._2.exists(_._2 == s"/docs/$matchString")).get._1
    val (document, setDocument) = useState(() =>{
        if (Main.isSSR) {
        Some(TrackSSRDocs.getPublic(docsFilePath(props)))
      } else if (js.typeOf(js.Dynamic.global.window.publicSSR) != "undefined") {
        js.Dynamic.global.window.publicSSR.asInstanceOf[js.Dictionary[String]].get(docsFilePath(props))
      } else None
    })

    useEffect(() => {
      val xhr = new XMLHttpRequest
      xhr.onload = _ => {
        setDocument(Some(xhr.responseText))
      }

      xhr.open("GET", docsFilePath(props))
      xhr.send()
    }, Seq(docsFilePath(props)))

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
            document.map { t =>
              Remark().use(ReactRenderer, literal(
                remarkReactComponents = literal(
                  h1 = RemarkH1.component: ReactComponentClass[_],
                  h2 = RemarkH2.component: ReactComponentClass[_],
                  code = RemarkCode.component: ReactComponentClass[_]
                )
              )).processSync(t).contents
            }
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
