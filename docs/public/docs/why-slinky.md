# Why Slinky
Slinky attempts to strike a balance between the JavaScript and Scala programming worlds, offering a complete API layer that mirrors the original JavaScript API for React and many extension points and accessory libraries for adding additional features on top of the core.

Although JavaScript, and thus React, is dynamically typed, Slinky provides a statically typed API that makes it possible to catch issues at compile time. In React itself, the documentation includes descriptions of an internal type system that, although it does not exist at runtime, can be adapted to form an actual type system for Slinky. Where possible, Slinky also adds additional type safety beyond the core React types in places like HTML tree construction.

To ensure the project succeeds in the short term and thrives in the long term, Slinky follows a set of core principles that guide its features:

1. **Modularity**: Allow developers to choose parts of Slinky that are appropriate for their apps
2. **Type safety for development experience**: Provide type safe facades whenever it can improve the coding experience, but not when it obscures the underlying concepts
3. **IDE Support**: Ensure that all Slinky features are well supported in major IDEs; place new features on hold if IDEs do not support their implementations
4. **Compatible with the Ecosystem**: Each feature must ensure that Slinky fits smoothly into the existing Scala and JavaScript ecosystems, integrating with existing tooling and community libraries
5. **Treat backward compatibility with utmost care**: Carefully design APIs to avoid breaking them in future. When breaking backwards compatibility outweighs the cost, document the breaking change clearly and follow the semantic versioning scheme so users aren’t caught off-guard.
6. **Quality**: Cover each feature with unit and integration tests to ensure that each new release is at least as good as the last one.
7. **Documentation**: Document each feature so that developers can learn about them and use them effectively.
