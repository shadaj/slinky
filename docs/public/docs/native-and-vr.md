# React Native and VR
Beginning with version 0.4.0, Slinky has official support for [React Native](http://facebook.github.io/react-native/) and [VR](http://facebook.github.io/react-vr/) through modules providing external component definitions for each.

## React Native
The `slinky-native` module contains component interfaces for React Native as well as Scala.js bindings to React Native APIs.

The easiest way to create a new native project is to use [Create React Native Scala App](https://github.com/shadaj/create-react-native-scala-app.g8). This template creates a starter project with a default React Native build configuration that enables hot reloading and bundling into a production app.

You can use this template from the command line with SBT:
```scala
sbt new shadaj/create-react-native-scala-app.g8
```

## React VR
Similarly, the `react-vr` module contains interfaces for React VR components and bindings for React VR APIs.

The easiest way to create a new VR project is to use [Create React VR Scala App](https://github.com/shadaj/create-react-vr-scala-app.g8). This template creates a starter project with a default React VR build configuration that enables hot reloading and bundling into a production app.

You can use this template from the command line with SBT:
```scala
sbt new shadaj/create-react-vr-scala-app.g8
```
