package me.shadaj.slinky.core.facade

import me.shadaj.slinky.core.{ExternalComponent, NoExternalProps}
import me.shadaj.slinky.core.annotations.react

@react object Fragment extends ExternalComponent {
  type Props = NoExternalProps

  override val component = React.Fragment
}
