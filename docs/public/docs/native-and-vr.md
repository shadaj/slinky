# React Native and VR
Beginning with version 0.4.0, Slinky has official support for [React Native](http://facebook.github.io/react-native/) and [VR](http://facebook.github.io/react-360/) through modules providing external component definitions for each.

## React Native
The `slinky-native` module contains component interfaces for React Native as well as Scala.js bindings to React Native APIs.

The easiest way to create a new native project is to use [Create React Native Scala App](https://github.com/shadaj/create-react-native-scala-app.g8). This template creates a starter project with a default React Native build configuration that enables hot reloading and bundling into a production app.

You can use this template from the command line with SBT:
```scala
sbt new shadaj/create-react-native-scala-app.g8
```

## React 360
Similarly, the `slinky-vr` module contains interfaces for React 360 components and bindings for React 360 APIs.

The easiest way to create a new VR project is to use [Create React VR Scala App](https://github.com/shadaj/create-react-vr-scala-app.g8). This template creates a starter project with a default React 360 build configuration that enables hot reloading and bundling into a production app.

You can use this template from the command line with SBT:
```scala
sbt new shadaj/create-react-vr-scala-app.g8
```
