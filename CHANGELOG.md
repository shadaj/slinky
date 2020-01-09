# Changelog

## vNEXT
* Add slinky-react-router and slinky-history as separate subprojects, to provide interfaces to react-router and the html5 history api [PR #305](https://github.com/shadaj/slinky/pull/305)
+ Fix project build in Windows OS and add automated testing in Windows to CI workflow. You can now build Slinky and run tests in Windows [PR #308](https://github.com/shadaj/slinky/pull/308)

## [v0.6.3](https://slinky.dev)
### Highlights :tada:
+ Improve experience using `ReactElement`s within first-order types such as Map and List [PR #285](https://github.com/shadaj/slinky/pull/285)
+ Support [React Native's Keyboard API](https://facebook.github.io/react-native/docs/keyboard) [PR #293](https://github.com/shadaj/slinky/pull/293)

### Bug Fixes
+ Fix crashes when calling `setState` on a component with `Props` set to some subtype of `Function1` [PR #295](https://github.com/shadaj/slinky/pull/295)
+ Support `useCallback` with a function that takes arguments [PR #290](https://github.com/shadaj/slinky/pull/290)
+ Fix false warnings on `@react` classes if `-Ywarn-value-discard` scalac option was used [PR #296](https://github.com/shadaj/slinky/pull/296)
+ Update the IntelliJ support plugin to be compatible with IntelliJ 2019.2 [PR #297](https://github.com/shadaj/slinky/pull/297)

## [v0.6.2](https://5d1e6bd54bf73a00097195a2--slinky.netlify.com/)
### Highlights :tada:
+ Support **optional attributes**, see [the docs](https://slinky.dev/docs/the-tag-api/) for more details [PR #275](https://github.com/shadaj/slinky/pull/275)
+ Slinky now has **full support for Scala 2.13** [PR #269](https://github.com/shadaj/slinky/pull/269)

### Bug Fixes
+ Improve error messages when an `@react class` extends the wrong supertype [PR #277](https://github.com/shadaj/slinky/pull/277)
+ Add shortcut `apply` for `@react` functional components to mirror class components [PR #278](https://github.com/shadaj/slinky/pull/278)

## [v0.6.1](https://5cb8c7064aba1f00090053e4--slinky.netlify.com/)
### Highlights :tada:
+ Allow `SetStateHookCallback` to be used as plain functions through an implicit conversion [PR #268](https://github.com/shadaj/slinky/pull/268)
+ Allow `React.memo` to specify a custom compare function [PR #260](https://github.com/shadaj/slinky/pull/260)
+ New facades for React Native: `SafeAreaView`, `TouchableHighlight`, `TouchableOpacity`, and `Platform` [PR #214](https://github.com/shadaj/slinky/pull/214)

### Bug Fixes
+ Allow `component` defined in a functional component to have any access control [PR #262](https://github.com/shadaj/slinky/pull/262)
+ Fix incorrect typing of `SyntheticMouseEvent.clientX` [PR #261](https://github.com/shadaj/slinky/pull/261)

## [v0.6.0](https://5c9e606a26ea4000070d0c7d--slinky.netlify.com/)
### Highlights :tada:
+ Casting the `target` property of event objects is **no longer needed**! Now all event handlers are statically typed based on the tag they are being placed on [PR #243](https://github.com/shadaj/slinky/pull/243)
+ Support **React Hooks for functional components**, see [the docs](https://slinky.dev/docs/functional-components-and-hooks/) for more details [PR #227](https://github.com/shadaj/slinky/pull/227)
+ Add support for **writing functional components**, see [the docs](https://slinky.dev/docs/functional-components-and-hooks/) for more details [PR #217](https://github.com/shadaj/slinky/pull/217)
+ Slinky docs have moved to a new domain, [slinky.dev](https://slinky.dev)!

### Breaking Changes :warning:
+ The tags API has seen some major changes, please take a look at the separate section below for more details [PR #243](https://github.com/shadaj/slinky/pull/243)
+ Half-built components and tags, which have some props provided but have not yet been converted to a `ReactElement`, cannot be reused. Doing this will result in a runtime exception pointing out where the reuse occured [PR #253](https://github.com/shadaj/slinky/pull/253)
+ Components using the static lifecycle functions `getDerivedStateFromProps` and `getDerivedStateFromError` must now override the functions with a `val` [PR #248](https://github.com/shadaj/slinky/pull/248)
+ The `React.forwardRef` function now takes a `FunctionalComponentTakingRef`, which can be creating by creating a functional component that takes an additional ref parameter: `FunctionalComponent((props, ref) => ...)` [PR #227](https://github.com/shadaj/slinky/pull/227)
+ The `ReactRef` type is no longer variant in its type parameters to increase type safety [PR #227](https://github.com/shadaj/slinky/pull/227)
+ Components will no longer have their `displayName` when built in `fullOptJS` mode, this results in a **~2.5% decrease in bundle size** and matches behavior with JS where names are obfuscated in production builds  [PR #217](https://github.com/shadaj/slinky/pull/217)
+ The `Option` writer now emits `null` instead of `js.undefined` for a value of `None` [PR #247](https://github.com/shadaj/slinky/pull/247)

### Bug Fixes
+ Make the static lifecycle functions `getDerivedStateFromProps` and `getDerivedStateFromError` work correctly in `fullOptJS` mode [PR #248](https://github.com/shadaj/slinky/pull/248)
+ Fix issues around state not updating when setting an `Option` to `None` in hot reloading mode [PR #247](https://github.com/shadaj/slinky/pull/247)
+ Handle `null` return values in derived state functions to not update the state [PR #249](https://github.com/shadaj/slinky/pull/249)
+ Fix the `js.|` reader/writer implementations to work correctly in `fullOptJS` mode [PR #248](https://github.com/shadaj/slinky/pull/248)
+ Support autoComplete attr for input and form elements [PR #225](https://github.com/shadaj/slinky/pull/225)
+ Fix capitalization of `rowSpan`/`colSpan` attribute (used to be `rowspan`/`colspan`) [PR #224](https://github.com/shadaj/slinky/pull/224)

### Tags API Changes
This release includes a major rewrite of the tags API that eliminates the need to cast the `target` value of event handlers and improves typesafety by more closely matching the official React API. As a result of these changes, event handlers defined in separate methods or code abstracting over tags may not initially compile with this new version.

#### Event Handlers
Event handlers in previous versions of Slinky were functions from a DOM event type to `Unit`. In React, events are normalized into `SyntheticEvent`s that provide a common subset of the events generated in different browsers. In this version of Slinky, event handlers have been changed to all be functions from a synthetic event type (one of `SyntheticEvent[TargetDOMType, UnderlyingDOMEventType]`, `SyntheticMouseEvent[TargetDOMType]`, `SyntheticTouchEvent[TargetDOMType]`, etc based on the event being listened to) to `Unit`. 

This means that explicit references to the DOM event type will no longer compile and will need to be replaced by an appropriate synthetic event type with a `TargetDOMType` that matches the DOM type of the tag the listener is being placed on. This change ensures that the properties available on event objects at compile time match those generated by React.

#### Abstracting over Tags
In previous versions of Slinky, passing in attributes required the first attribute to be passed separately from the varargs parameter containing the rest of the attributes. In this version, the API for constructing tags has changed from `apply` taking either `(firstTag: AttrPair[...], otherTags: AttrPair[...]*)` or `ReactElement*` to just a single method taking `(mods: TagMod[...]*)` with conversions from `AttrPair` and `ReactElement` to this type.

So code that passed in generated attributes like:

```scala
div(firstAttr, restAttrs: _*)
```

will now need to be replaced by

```scala
div(allAttrs: _*)
```

Similarly, in components that previously took in `AttrPair` and `ReactElement` separately, this can now be simplified to just take `TagMod`s.

## [v0.5.2](https://5c4f690f534c280008c14654--slinky.netlify.com/)
### Highlights :tada:
+ Slinky support for Scala 2.13 is now built with **2.13.0-M5** [PR #202](https://github.com/shadaj/slinky/pull/202)
+ Add support for `getDerivedStateFromError` from React 16.6 [PR #206](https://github.com/shadaj/slinky/pull/206)
+ Add support for the `Suspense` component from React 16.6 [PR #216](https://github.com/shadaj/slinky/pull/216)
+ Use default parameters to fill in missing props on exported components [PR #221](https://github.com/shadaj/slinky/pull/221)

### Bug Fixes
+ Fix compile errors when overriding the `Props` or `State` type [PR #220](https://github.com/shadaj/slinky/pull/219)
+ Update library injector setup to support IntelliJ 2018.3 and newer [PR #219](https://github.com/shadaj/slinky/pull/219)
+ Fix runtime crash when `initialState()` is defined with the extra parentheses [PR #218](https://github.com/shadaj/slinky/pull/218)
+ Fix compilation errors in generating `Reader`/`Writer` when private objects are involved [PR #205](https://github.com/shadaj/slinky/pull/205)

## [v0.5.1](https://5bddfe8373f2cf373856d7f7--slinky.netlify.com/)
### Highlights :tada:
+ Support reading and writing `scala.Array` [PR #187](https://github.com/shadaj/slinky/pull/187)
+ Support the `defaultValue` attribute for specifying a default form value without overriding user inputs [PR #186](https://github.com/shadaj/slinky/pull/186)

### Bug Fixes
+ Prevent crashes with components that store an `Option` of an opaque type in their `State` [PR #198](https://github.com/shadaj/slinky/pull/198)
+ Fix `ComponentWrapper`s not picking up manually defined Reader/Writers for the `State` type [PR #190](https://github.com/shadaj/slinky/pull/190)
+ Fix how the IntelliJ extensions handles components that have a `children` prop but no other props [PR #189](https://github.com/shadaj/slinky/pull/189)
+ Fix errors when using the `@react` macro annotation with `Component`/`StatelessComponent` imported locally [PR #188](https://github.com/shadaj/slinky/pull/188)
+ Fix the `value` attribute not being available on the `select` and `textarea` tags [PR #177](https://github.com/shadaj/slinky/pull/177)
+ Bump Scala version to 2.12.7 and SBT/plugin versions as well [PR #176](https://github.com/shadaj/slinky/pull/176)

## v0.5.0
### Highlights :tada:
+ Slinky now supports **Scala 2.13.0-M4** [PR #153](https://github.com/shadaj/slinky/pull/153)!
+ Magnolia has been replaced with a custom implementation tuned for Slinky, resulting in smaller bundles and faster compilation [PR #159](https://github.com/shadaj/slinky/pull/159), [PR #159](https://github.com/shadaj/slinky/pull/159) 
+ Readers and writers for props are no longer needed for hot-reloading components, resulting in **up to 2x drops** in bundle size in `fastOptJS` mode [PR #162](https://github.com/shadaj/slinky/pull/162)
+ React element construction is now more aggressively inlined, resulting in smaller bundle sizes (5% drop in the docs project) [PR #156](https://github.com/shadaj/slinky/pull/156)
+ Switch from React VR package to React 360 [PR #141](https://github.com/shadaj/slinky/pull/141)

### Breaking Changes
+ Scalameta is no longer used for the `@react` macro, and Macro Paradise is used instead. See the docs for updated installation instructions for adding the Macro Paradise compiler plugin [PR #132](https://github.com/shadaj/slinky/pull/132)
+ `@react` components taking a `children` prop now generate an `apply` method with the children moved to a curried parameter to better match JSX [PR #161](https://github.com/shadaj/slinky/pull/161)
+ React VR components are no longer supported, the `slinky-vr` module now points to React 360 [PR #141](https://github.com/shadaj/slinky/pull/141)
+ `ReactComponentClass` now takes a type parameter of the `Props` type to improve type safety with higher-order components. Existing uses can be safely replaced with `ReactComponentClass[_]` [PR #157](https://github.com/shadaj/slinky/pull/157)
+ Interop with Scala.js React now requires using the explicit conversions `.toSlinky` and `.toScalaJSReact` [PR #151](https://github.com/shadaj/slinky/pull/151)

## v0.4.3
+ Support pointer events that were added in React 16.4 [PR #149](https://github.com/shadaj/slinky/pull/149)
+ Bump Scala.js React version for interop to 1.2.0 [PR #148](https://github.com/shadaj/slinky/pull/148)
+ Fix errors in Reader/Writer provider macros with Scala versions > 2.12.4 [PR #147](https://github.com/shadaj/slinky/pull/147)
+ Support storing any type as a default context value [PR #136](https://github.com/shadaj/slinky/pull/136)
+ Set sourcemaps to use GitHub URLs so that they load in other apps [PR #143](https://github.com/shadaj/slinky/pull/143)

## v0.4.2
+ Fix bug with `shouldComponentUpdate` not being registered correctly on the component [PR #135](https://github.com/shadaj/slinky/pull/135)

## v0.4.1
+ Fix exception when hot-reloading Slinky components [PR #134](https://github.com/shadaj/slinky/pull/134)

## v0.4.0
### Highlights :tada:
+ **Slinky now has support for React 16.3 features**
  + Use the new Context API with a [statically-typed interface](https://slinky.shadaj.me/docs/context/) [PR #125](https://github.com/shadaj/slinky/pull/125)
  + Use the new Ref API with a [statically-typed interface](https://slinky.shadaj.me/docs/refs/) as well! [PR #126](https://github.com/shadaj/slinky/pull/126)
  + Transition to the new React lifecycle with support for [getSnapshotBeforeUpdate](https://reactjs.org/docs/react-component.html#getsnapshotbeforeupdate) [PR #129](https://github.com/shadaj/slinky/pull/129)
  + Use the [getDerivedStateFromProps](https://reactjs.org/docs/react-component.html#static-getderivedstatefromprops) API by defining it inside `ComponentWrapper` or the companion object of an annotated component [PR #130](https://github.com/shadaj/slinky/pull/130)
  + Use the `React.forwardRef` API to create new components that forward their refs to children [PR #127](https://github.com/shadaj/slinky/pull/127)
  + Use the `StrictMode` component to enable more runtime checks on your components [PR #128](https://github.com/shadaj/slinky/pull/128)
+ **Slinky now has support for React Native**, available in the `slinky-native` module. Try it out with [create-react-native-scala-app](https://github.com/shadaj/create-react-native-scala-app.g8)
+ **Slinky now has support for React VR**, available in the `slinky-vr` module. Try it out with [create-react-vr-scala-app](https://github.com/shadaj/create-react-vr-scala-app.g8)
+ Want to write fancier unit tests for your Slinky app? Slinky now comes with an interface for `react-test-renderer`, available under the `slinky-testrenderer` module. [PR #119](https://github.com/shadaj/slinky/pull/119)

### Breaking Changes
+ **The `ErrorBoundary` trait has been removed, because it is no longer needed to implement an error boundary component**
+ The `DefinitionBase` class now takes an additional type parameter `Snapshot`, for use with the new snapshot-based lifecycle API 
+ The `BuildingComponent` case class has been simplified into a regular class, so the `new` keyword is now required when creating instances
+ The `React` object has been refactored to take regular Scala types instead of JS types, so any dependency on the original JS types (`js.FunctionN`) will not work

### Details
+ The `@react` macro now produces nicer APIs for external components that have default values for all props parameters. [PR #119](https://github.com/shadaj/slinky/pull/119)
+ Add more variations for `ExternalComponent` that support providing a statically-typed interface for the component instance: `ExternalComponentWithRefType`, `ExternalComponentWithAttributesWithRefType`, `ExternalComponentNoPropsWithRefType`, `ExternalComponentNoPropsWithAttributesWithRefType` [PR #119](https://github.com/shadaj/slinky/pull/119)
+ Bring back the `WithRaw` trait, which makes it possible to access the original object of a read value [PR #122](https://github.com/shadaj/slinky/pull/122)
+ Fix exceptions when declaring custom tags and attributes in a component class [PR #118](https://github.com/shadaj/slinky/pull/118)
+ Fix exceptions when reading the null-prototype in Node.js [PR #121](https://github.com/shadaj/slinky/pull/121)

## v0.3.2
+ Improve support for creating custom tags and attributes (see docs for details) [PR #116](https://github.com/shadaj/slinky/pull/116)

## v0.3.1
+ Fix compilation errors when using an `Option` of a component instance in a tag tree [PR #111](https://github.com/shadaj/slinky/pull/111)
+ Reduce warnings for unused imports when using the `@react` macro annotation [PR #112](https://github.com/shadaj/slinky/pull/112)

## v0.3.0
### Highlights
+ Slinky now has **full support for React 16** features such as fragments, portals, and streaming server-side-rendering
+ The tag API has been remodeled to be more efficient and flexible (see https://slinky.shadaj.me/docs/abstracting-over-tags/)
+ The `@react` macro annotation is now compatible with many more use cases, such as pulling values from a companion object, and has improved support in IntelliJ

### Details
+ **BREAKING!**: The package `me.shadaj.slinky` has been renamed to `slinky` [PR #103](https://github.com/shadaj/slinky/pull/103)
+ **BREAKING**: Stateless components that use the `@react` macro annotation must extend the `StatelessComponent` class instead of just `Component` [PR #69](https://github.com/shadaj/slinky/pull/69)
+ **BREAKING**: Callbacks passed to `setState` are now Scala functions, so there is no need to force implicit conversions [PR #71](https://github.com/shadaj/slinky/pull/71)
+ **BREAKING**: The tag construction flow now requires attributes to come before children. In addition, an empty list of attributes is no longer allowed. When generating tags with dynamic attributes, you will now need to construct the tag as `tag(attrs.head, attrs.tail: _*)` to satisfy this requirement [PR #73](https://github.com/shadaj/slinky/pull/73)
+ Add support for portal elements, which were introduced in React 16 [PR #65](https://github.com/shadaj/slinky/pull/65)
+ Greatly improve IntelliJ support for Slinky with special macro annotation behavior [PR #69](https://github.com/shadaj/slinky/pull/69)
+ Add an alternative `apply` method to eliminate compiler warnings when using propless components [PR #70](https://github.com/shadaj/slinky/pull/70)
+ Add better error message when `@react` annotation is used on a component with no `Props` type declaration [PR #72](https://github.com/shadaj/slinky/pull/72)
+ Better support for converting Slinky types to scalajs-react types when an implicit conversion to `ReactElement` is needed [PR #73](https://github.com/shadaj/slinky/pull/73)
+ Large performance gains in tag construction, with over 5x improvements for some components! [PR #73](https://github.com/shadaj/slinky/pull/73)
+ Add missing global HTML attributes: `spellCheck`, `contentEditable`, and `tabIndex` [PR #77](https://github.com/shadaj/slinky/pull/77)
+ Fix compilation errors when trying to use findDOMNode and passing in an annotated component [PR #78](https://github.com/shadaj/slinky/pull/78)
+ Add no-callback forceUpdate and make it available in annotated components [PR #78](https://github.com/shadaj/slinky/pull/78)
+ Fix bugs involving using companion object values from a `@react` annotated component [PR #80](https://github.com/shadaj/slinky/pull/80)
+ Add a `*` tag for external components that can take any attribute [PR #81](https://github.com/shadaj/slinky/pull/81)
+ Add support for error boundaries, which were added in React 16 [PR #82](https://github.com/shadaj/slinky/pull/82)
+ Add support for all `ReactElement` types introduced in React 16, such as numbers and booleans [PR #83](https://github.com/shadaj/slinky/pull/83)
+ Add remaining methods from ReactDOMServer, including those introduced in React 16 [PR #84](https://github.com/shadaj/slinky/pull/84)
+ Add the custom `on` attribute for AMP pages, introduced in React 16 [PR #85](https://github.com/shadaj/slinky/pull/85)
+ Add facade for `React.Children`, including a new type `ReactChildren` for `props.children` [PR #86](https://github.com/shadaj/slinky/pull/86)
+ Add facade for `ReactDOM.unmountComponentAtNode` [PR #88](https://github.com/shadaj/slinky/pull/88)
+ Fix mapping of undefined values in a case class. Such values now do not become a property in the written object [PR #95](https://github.com/shadaj/slinky/pull/95)
+ Add readers for `js.Array[T]` [PR #100](https://github.com/shadaj/slinky/pull/100)
+ Add common supertype `Tag` for all tag elements to allow abstracting over them [PR #101](https://github.com/shadaj/slinky/pull/101)
+ Add common supertype `Attr` with the typeclass `supports[Tag]` to allow abstracting over supported attributes (see `TagTest` for example) [PR #101](https://github.com/shadaj/slinky/pull/101)

## v0.2.0
+ **BREAKING**: Instead of taking key and refs as additional parameters next to props, they are now taken in through the methods `withKey` and `withRef` (components and external components only)
+ **BREAKING**: Introduce the experimental macro annotation `@react` to simplify component and external component creation with auto-generated companion object for a component class (or external component object). This is a major change to how applications with Slinky are written, so please see the notes at the end of the changelog [PR #29](https://github.com/shadaj/slinky/pull/29)
  + **BREAKING**: This change also renames the `Component` class to `ComponentWrapper`. The `Component` class is now used for the `@react` annotation.
+ **BREAKING**: Rename `ExternalComponentWithTagMods` to `ExternalComponentWithAttributes` and take attributes as a curried parameter instead of an extra parameter after `Props` [PR #26](https://github.com/shadaj/slinky/pull/26)
+ **BREAKING**: Introduce `ExternalComponentNoProps` and `ExternalComponentNoPropsWithAttributes` for cases where an external component takes no props [PR #58](https://github.com/shadaj/slinky/pull/58)
+ **BREAKING**: Slinky now expects that the `-P:scalajs:sjsDefinedByDefault` compiler option is enabled in the `@react` macro annotation []
+ Have mouse attributes such as `onMouseDown` take a `MouseEvent` instead of just an `Event` [PR #27](https://github.com/shadaj/slinky/pull/27)
+ Add support for generating `Reader` and `Writer` for sealed traits, value classes, and case objects (through a Magnolia upgrade) [PR #45](https://github.com/shadaj/slinky/pull/45)
+ Fix bug with hot loading not updating instances of readers and writers [PR #49](https://github.com/shadaj/slinky/pull/49)
+ Fix bug with hot loading using the wrong proxy component when there are multiple components classes in the tree [PR #50](https://github.com/shadaj/slinky/pull/50)
+ Add support for reading and writing js.Dynamic (and anything that extends js.Any) [PR #51](https://github.com/shadaj/slinky/pull/51)
+ Add support for reading and writing union types (js.|) [PR #52](https://github.com/shadaj/slinky/pull/52)
+ Slinky's implementation of mapping Scala types to JS types is now available as a separate module `slinky-readwrite` [PR #54](https://github.com/shadaj/slinky/pull/54)
+ Improve type safety of ExternalComponentWithAttributes by restricting the type parameter to tag types [PR #55](https://github.com/shadaj/slinky/pull/55)

### `@react` macro annotation (experimental)
One of Slinky's main goals is to have React components written in Scala look very similar to ES6. In version 0.1.x, Slinky required
extra boilerplate for defining an object that contained `apply` methods and then creating a `Def` inner class that contained the actual component logic.

This version includes the `@react` macro annotation, which makes it possible to directly write the class containing component logic and have Slinky generate
the companion object for constructing component instances. The macro annotation also now generates special `apply` methods when your Props is a case class
so that constructing Scala components looks more similar to JSX, with the Props values directly taken as parameters of the `apply`.

Note that the macro annotation is **experimental** and **not required**. To use the original component style simply replace the `extends Component` with `extends ComponentWrapper` and your
components should continue to function as they did before.

As an example of migrating an existing component to the new macro annotation style, take a simple component that displays a header:
```scala
import me.shadaj.slinky.core.WrapperComponent
import me.shadaj.slinky.web.html._

object HelloMessage extends WrapperComponent {
  case class Props(name: String)
  type State = Unit

  @ScalaJSDefined
  class Def(jsProps: js.Object) extends Definition(jsProps) {
    def render() = {
      div(s"Hello ${props.name}")
    }
  }
}
``` 

to use the new macro annotation style, we essentially extract out the definition class, move the `Props` and `State` types into the class, and extend `Component` instead of `Definition`:
```scala
import me.shadaj.core.{Component, react}
import me.shadaj.slinky.web.html._

@react class HelloMessage extends Component {
  case class Props(name: String)
  type State = Unit
  
  def render() = {
    div(s"Hello ${props.name}")
  }
}
```

If we want to use this component, we now have a new option for constructing it directly passing in the Props values
```scala
HelloMessage(HelloMessage.Props("Shadaj")) // old style
HelloMessage("Shadaj") // now possible!
HelloMessage(name = "Shadaj") // now possible, closest to JSX
```

The `@react` annotation is also available for external components. For external components, the annotation generates the new `apply` method style in the same style as Scala components.
```scala
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.ExternalComponent

@react object React3 extends ExternalComponent {
  case class Props(mainCamera: String, width: Int, height: Int,
                   onAnimate: Option[() => Unit] = None, alpha: Boolean = false)

  override val component: js.Object = js.Dynamic.global.React3.asInstanceOf[js.Object]
}
```

this makes it possible to construct the external component as
```scala
React3(mainCamera = "camera", width = 800, height = 800)
```

## v0.1.1
+ Have ExternalComponentsWithTagMods take the tag type as a type parameter instead of an abstract type [PR #19](https://github.com/shadaj/slinky/pull/19)
+ Added support for reading and writing values of type `js.UndefOr[T]` [PR #18](https://github.com/shadaj/slinky/pull/18)
+ Components and external components with a `Props` type of `Unit` can now be constructed without any parameters, instead of having to pass in `()` as props [PR #12](https://github.com/shadaj/slinky/pull/12)
+ Boolean attributes, such as `disabled`, can now be used without specifying a value to closer match JSX. For example, a disabled input can now be constructed as `input(disabled)` without providing the `:= true` [PR #14](https://github.com/shadaj/slinky/pull/14)

## v0.1.0
+ Initial release
