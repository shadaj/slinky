# Electron
Slinky web projects can also be easily published as cross-platform desktop apps via [Electron](https://www.electronjs.org/).

## Creating a New Electron Project
The easiest way to create a new Electron project is to use the [`electron` branch of Create React Scala App](https://github.com/shadaj/create-react-scala-app.g8/tree/electron). This template adds to the starter project the needed NPM packages and configuration files for your bundle to be made into an Electron app.

You can use this template from the command line, having SBT and NPM:
```shell
sbt new shadaj/create-react-scala-app.g8 --branch electron
```

Then build the app, install the dependencies and make it with Electron.
```shell
sbt build
```

```shell
npm install
```

```shell
npm run make
```
