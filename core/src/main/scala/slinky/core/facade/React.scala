package slinky.core.facade

import slinky.core._
import slinky.readwrite.{ObjectOrWritten, Reader, Writer}

import scala.scalajs.js
import js.|
import scala.annotation.unchecked.uncheckedVariance
import scala.scalajs.js.annotation.{JSImport, JSName}
import scala.scalajs.js.JSConverters._
import scala.language.implicitConversions

@js.native
trait ReactElement extends TagMod[Any]

// mixed into TagMod, because then it's available for both mods and regular conversions
trait ReactElementConversions {
  @inline final implicit def stringToElement(s: String): ReactElement = {
    s.asInstanceOf[ReactElement]
  }

  @inline final implicit def intToElement(i: Int): ReactElement = {
    i.asInstanceOf[ReactElement]
  }

  @inline final implicit def doubleToElement(d: Double): ReactElement = {
    d.asInstanceOf[ReactElement]
  }

  @inline final implicit def floatToElement(f: Float): ReactElement = {
    f.asInstanceOf[ReactElement]
  }

  @inline final implicit def booleanToElement(b: Boolean): ReactElement = {
    b.asInstanceOf[ReactElement]
  }

  final implicit def optionToElement[T](s: Option[T])(implicit cv: T => ReactElement): ReactElement = {
    s match {
      case Some(e) => cv(e)
      case None => null.asInstanceOf[ReactElement]
    }
  }

  final implicit def seqElementToElement[T](s: Iterable[T])(implicit cv: T => ReactElement): ReactElement = {
    val elem = js.Array[ReactElement]()
    s.foreach(v => elem.push(cv(v)))
    elem.asInstanceOf[ReactElement]
  }
}

@js.native
trait ReactInstance extends js.Object

@js.native
trait ReactChildren extends ReactElement

@js.native
trait ReactRef[T] extends js.Object {
  var current: T @uncheckedVariance  = js.native
}

@js.native
@JSImport("react", JSImport.Namespace, "React")
object ReactRaw extends js.Object {
  def createElement(elementName: String | js.Object,
                    properties: js.Dictionary[js.Any],
                    contents: ReactElement*): ReactElement = js.native

  val createElement: js.Dynamic = js.native // used for WithAttrs

  def createContext[T](defaultValue: T): ReactContext[T] = js.native

  def createRef[T](): ReactRef[T] = js.native

  def forwardRef[P](fn: js.Object): js.Object = js.native

  def memo(fn: js.Function, compare: js.UndefOr[js.Object]): js.Function = js.native

  @js.native
  object Children extends js.Object {
    def map(children: ReactChildren, transformer: js.Function1[ReactElement, ReactElement]): ReactChildren = js.native
    def map(children: ReactChildren, transformer: js.Function2[ReactElement, Int, ReactElement]): ReactChildren = js.native

    def forEach(children: ReactChildren, transformer: js.Function1[ReactElement, Unit]): Unit = js.native
    def forEach(children: ReactChildren, transformer: js.Function2[ReactElement, Int, Unit]): Unit = js.native

    def only(children: ReactChildren): ReactElement = js.native

    def count(children: ReactChildren): Int = js.native

    def toArray(children: ReactChildren): js.Array[ReactElement] = js.native
  }

  val Fragment: js.Object = js.native
  val StrictMode: js.Object = js.native
  val Suspense: js.Object = js.native
}

object React {
  def createElement(elementName: String | js.Object,
                    properties: js.Dictionary[js.Any],
                    contents: ReactElement*): ReactElement = ReactRaw.createElement(elementName, properties, contents: _*)

  def createContext[T](defaultValue: T): ReactContext[T] = ReactRaw.createContext[T](defaultValue)

  def createRef[T]: ReactRef[T] = ReactRaw.createRef[T]()

  def forwardRef[P, R](component: FunctionalComponentTakingRef[P, R]): FunctionalComponentForwardedRef[P, R] = {
    new FunctionalComponentForwardedRef(ReactRaw.forwardRef(component.component))
  }

  def memo[P](component: FunctionalComponent[P]): FunctionalComponent[P] = {
    new FunctionalComponent(ReactRaw.memo(component.component, js.undefined))
  }

  def memo[P](component: FunctionalComponent[P], compare: (P, P) => Boolean): FunctionalComponent[P] = {
    new FunctionalComponent(ReactRaw.memo(component.component, ((oldProps: js.Dynamic, newProps: js.Dynamic) => {
      compare(oldProps.__.asInstanceOf[P], newProps.__.asInstanceOf[P])
    }): js.Function2[js.Dynamic, js.Dynamic, Boolean]))
  }

  @JSImport("react", "Component", "React.Component")
  @js.native
  class Component(jsProps: js.Object) extends js.Object {
    def forceUpdate(): Unit = js.native
    def forceUpdate(callback: js.Function0[Unit]): Unit = js.native
  }

  object Children extends js.Object {
    def map(children: ReactChildren, transformer: ReactElement => ReactElement): ReactChildren = {
      ReactRaw.Children.map(children, transformer)
    }

    def map(children: ReactChildren, transformer: (ReactElement, Int) => ReactElement): ReactChildren = {
      ReactRaw.Children.map(children, transformer)
    }

    def forEach(children: ReactChildren, transformer: ReactElement => Unit): Unit = {
      ReactRaw.Children.forEach(children, transformer)
    }

    def forEach(children: ReactChildren, transformer: (ReactElement, Int) => Unit): Unit = {
      ReactRaw.Children.forEach(children, transformer)
    }

    def only(children: ReactChildren): ReactElement = {
      ReactRaw.Children.only(children)
    }

    def count(children: ReactChildren): Int = {
      ReactRaw.Children.count(children)
    }

    def toArray(children: ReactChildren): js.Array[ReactElement] = {
      ReactRaw.Children.toArray(children)
    }
  }
}

@js.native
@JSImport("react", JSImport.Namespace, "React")
private[slinky] object HooksRaw extends js.Object {
  def useState[T](default: T | js.Function0[T]): js.Tuple2[T, js.Function1[js.Any, Unit]] = js.native
  
  def useEffect(thunk: js.Function0[EffectCallbackReturn]): Unit = js.native
  def useEffect(thunk: js.Function0[EffectCallbackReturn], watchedObjects: js.Array[js.Any]): Unit = js.native
  
  def useContext[T](context: ReactContext[T]): T = js.native

  def useReducer[T, A](reducer: js.Function2[T, A, T], initialState: T): js.Tuple2[T, js.Function1[A, Unit]] = js.native
  def useReducer[T, I, A](reducer: js.Function2[T, A, T], initialState: I, init: js.Function1[I, T]): js.Tuple2[T, js.Function1[A, Unit]] = js.native

  def useCallback(callback: js.Function0[Unit], watchedObjects: js.Array[js.Any]): js.Function0[Unit] = js.native

  def useMemo[T](callback: js.Function0[T], watchedObjects: js.Array[js.Any]): T = js.native

  def useRef[T](initialValue: T): ReactRef[T] = js.native

  def useImperativeHandle[R](ref: ReactRef[R], value: js.Function0[R]): Unit = js.native

  def useLayoutEffect(thunk: js.Function0[EffectCallbackReturn]): Unit = js.native
  def useLayoutEffect(thunk: js.Function0[EffectCallbackReturn], watchedObjects: js.Array[js.Any]): Unit = js.native

  def useDebugValue(value: String): Unit = js.native
}

@js.native trait EffectCallbackReturn extends js.Object
object EffectCallbackReturn {
  @inline implicit def fromFunction[T](fn: () => T): EffectCallbackReturn = {
    (fn: js.Function0[T]).asInstanceOf[EffectCallbackReturn]
  }

  @inline implicit def fromAny[T](value: T): EffectCallbackReturn = {
    js.undefined.asInstanceOf[EffectCallbackReturn]
  }
}

final class SetStateHookCallback[T](private val origFunction: js.Function1[js.Any, Unit]) extends AnyVal {
  @inline def apply(newState: T): Unit = {
    origFunction.apply(newState.asInstanceOf[js.Any])
  }

  @inline def apply(transformState: T => T): Unit = {
    origFunction.apply(transformState: js.Function1[T, T])
  }
}

object SetStateHookCallback {
  @inline implicit def toFunction[T](callback: SetStateHookCallback[T]): T => Unit = callback(_)

  @inline implicit def toTransformFunction[T](callback: SetStateHookCallback[T]): (T => T) => Unit = callback(_)
}

object Hooks {
  @inline def useState[T](default: T): (T, SetStateHookCallback[T]) = {
    val call = HooksRaw.useState[T](default)
    (call._1, new SetStateHookCallback[T](call._2))
  }

  @inline def useState[T](lazyDefault: () => T): (T, SetStateHookCallback[T]) = {
    val call = HooksRaw.useState[T](lazyDefault: js.Function0[T])
    (call._1, new SetStateHookCallback[T](call._2))
  }

  @inline def useEffect[T](thunk: () => T)(implicit conv: T => EffectCallbackReturn): Unit = {
    HooksRaw.useEffect(() => { conv(thunk()) })
  }

  @inline def useEffect[T](thunk: () => T, watchedObjects: Iterable[Any])(implicit conv: T => EffectCallbackReturn): Unit = {
    HooksRaw.useEffect(
      () => { conv(thunk()) },
      watchedObjects.toJSArray.asInstanceOf[js.Array[js.Any]]
    )
  }

  @inline def useContext[T](context: ReactContext[T]): T = HooksRaw.useContext[T](context)

  @inline def useReducer[T, A](reducer: (T, A) => T, initialState: T): (T, A => Unit) = {
    val ret = HooksRaw.useReducer[T, A](reducer, initialState)
    (ret._1, ret._2)
  }

  @inline def useReducer[T, I, A](reducer: (T, A) => T, initialState: I, init: I => T): (T, A => Unit) = {
    val ret = HooksRaw.useReducer[T, I, A](reducer, initialState, init)
    (ret._1, ret._2)
  }

  @inline def useCallback(callback: () => Unit, watchedObjects: Iterable[Any]): () => Unit = {
    HooksRaw.useCallback(callback, watchedObjects.toJSArray.asInstanceOf[js.Array[js.Any]])
  }

  @inline def useMemo[T](memoValue: () => T, watchedObjects: Iterable[Any]): T = {
    HooksRaw.useMemo[T](memoValue, watchedObjects.toJSArray.asInstanceOf[js.Array[js.Any]])
  }

  @inline def useRef[T](initialValue: T): ReactRef[T] = {
    HooksRaw.useRef[T](initialValue)
  }

  @inline def useImperativeHandle[R](ref: ReactRef[R], value: () => R): Unit = {
    HooksRaw.useImperativeHandle[R](ref, value)
  }

  @inline def useLayoutEffect[T](thunk: () => T)(implicit conv: T => EffectCallbackReturn): Unit = {
    HooksRaw.useLayoutEffect(() => { conv(thunk()) })
  }

  @inline def useLayoutEffect[T](thunk: () => T, watchedObjects: Iterable[Any])(implicit conv: T => EffectCallbackReturn): Unit = {
    HooksRaw.useLayoutEffect(
      () => { conv(thunk()) },
      watchedObjects.toJSArray.asInstanceOf[js.Array[js.Any]]
    )
  }

  @inline def useDebugValue(value: String): Unit = HooksRaw.useDebugValue(value)
}

@js.native
trait ErrorBoundaryInfo extends js.Object {
  val componentStack: String = js.native
}

@js.native
trait PrivateComponentClass extends js.Object {
  @JSName("props")
  var propsR: js.Object = js.native

  @JSName("state")
  var stateR: js.Object = js.native

  @JSName("refs")
  val refsR: js.Dynamic = js.native

  @JSName("context")
  val contextR: js.Dynamic = js.native

  @JSName("setState")
  def setStateR(newState: js.Object): Unit = js.native

  @JSName("setState")
  def setStateR(fn: js.Function2[js.Object, js.Object, js.Object]): Unit = js.native

  @JSName("setState")
  def setStateR(newState: js.Object, callback: js.Function0[Unit]): Unit = js.native

  @JSName("setState")
  def setStateR(fn: js.Function2[js.Object, js.Object, js.Object], callback: js.Function0[Unit]): Unit = js.native
}
