package me.shadaj.slinky.generator

object SVG extends TagsProvider {
  val extraAttributes = MDN.extraAttributes
  
  val allAttributes =
    """accent-height accumulate additive alphabetic amplitude arabic-form ascent attributeName
      |attributeType azimuth baseFrequency baseProfile bbox begin bias by calcMode cap-height
      |class clipPathUnits contentScriptType contentStyleType cx cy d descent diffuseConstant
      |divisor dur dx dy edgeMode elevation end exponent externalResourcesRequired fill filterRes
      |filterUnits font-family font-size font-stretch font-style format from fx fy g1 g2 glyph-name
      |glyphRef gradientTransform gradientUnits hanging height horiz-adv-x horiz-origin-x horiz-origin-y
      |id ideographic in in2 intercept k k1 k2 k3 k4 kernelMatrix kernelUnitLength keyPoints keySplines
      |keyTimes lang lengthAdjust limitingConeAngle local markerHeight markerUnits markerWidth maskContentUnits
      |maskUnits mathematical max media method min mode name numOctaves offset operator order orient
      |orientation origin overline-position overline-thickness panose-1 path pathLength patternContentUnits
      |patternTransform patternUnits points pointsAtX pointsAtY pointsAtZ preserveAlpha preserveAspectRatio primitiveUnits
      |r radius refX refY rendering-intent repeatCount repeatDur requiredExtensions requiredFeatures restart result
      |rotate rx ry scale seed slope spacing specularConstant specularExponent spreadMethod startOffset stdDeviation stemh
      |stemv stitchTiles strikethrough-position strikethrough-thickness string style surfaceScale systemLanguage
      |tableValues target targetX targetY textLength title to transform type u1 u2 underline-position
      |underline-thickness unicode unicode-range units-per-em v-alphabetic v-hanging v-ideographic
      |v-mathematical values version vert-adv-y vert-origin-x vert-origin-y viewBox viewTarget width
      |widths x x-height x1 x2 xChannelSelector xlink xml y y1 y2 yChannelSelector z zoomAndPan
      |alignment-baseline baseline-shift clip-path clip-rule clip color-interpolation-filters
      |color-interpolation color-profile color-rendering color cursor direction display dominant-baseline
      |enable-background fill-opacity fill-rule filter flood-color flood-opacity font-size-adjust
      |font-variant font-weight glyph-orientation-horizontal glyph-orientation-vertical image-rendering
      |kerning letter-spacing lighting-color marker-end marker-mid marker-start mask opacity overflow
      |pointer-events shape-rendering stop-color stop-opacity stroke-dasharray stroke-dashoffset
      |stroke-linecap stroke-linejoin stroke-miterlimit stroke-opacity stroke-width stroke text-anchor
      |text-decoration text-rendering unicode-bidi visibility word-spacing writing-mode"""
      .stripMargin.split('\n').flatMap(_.split(' '))

  val supportedTags =
    """a altGlyph altGlyphDef altGlyphItem animate animateColor animateMotion animateTransform
      |circle clipPath cursor defs desc ellipse feBlend feColorMatrix feComponentTransfer
      |feComposite feConvolveMatrix feDiffuseLighting feDisplacementMap feDistantLight feFlood
      |feFuncA feFuncB feFuncG feFuncR feGaussianBlur feImage feMerge feMergeNode feMorphology
      |feOffset fePointLight feSpecularLighting feSpotLight feTile feTurbulence filter
      |font foreignObject g glyph glyphRef hkern image line linearGradient marker mask metadata mpath
      |path pattern polygon polyline radialGradient rect script set stop style svg switch symbol text textPath
      |title tref tspan use view vkern""".stripMargin.split('\n').flatMap(_.split(' '))

  val supportedAttributes =
    """accentHeight accumulate additive alignmentBaseline allowReorder alphabetic
      |amplitude arabicForm ascent attributeName attributeType autoReverse azimuth
      |baseFrequency baseProfile baselineShift bbox begin bias by calcMode capHeight
      |clip clipPath clipPathUnits clipRule colorInterpolation
      |colorInterpolationFilters colorProfile colorRendering contentScriptType
      |contentStyleType cursor cx cy d decelerate descent diffuseConstant direction
      |display divisor dominantBaseline dur dx dy edgeMode elevation enableBackground
      |end exponent externalResourcesRequired fill fillOpacity fillRule filter
      |filterRes filterUnits floodColor floodOpacity focusable fontFamily fontSize
      |fontSizeAdjust fontStretch fontStyle fontVariant fontWeight format from fx fy
      |g1 g2 glyphName glyphOrientationHorizontal glyphOrientationVertical glyphRef
      |gradientTransform gradientUnits hanging horizAdvX horizOriginX ideographic
      |imageRendering in in2 intercept k k1 k2 k3 k4 kernelMatrix kernelUnitLength
      |kerning keyPoints keySplines keyTimes lengthAdjust letterSpacing lightingColor
      |limitingConeAngle local markerEnd markerHeight markerMid markerStart
      |markerUnits markerWidth mask maskContentUnits maskUnits mathematical mode
      |numOctaves offset opacity operator order orient orientation origin overflow
      |overlinePosition overlineThickness paintOrder panose1 pathLength
      |patternContentUnits patternTransform patternUnits pointerEvents points
      |pointsAtX pointsAtY pointsAtZ preserveAlpha preserveAspectRatio primitiveUnits
      |r radius refX refY renderingIntent repeatCount repeatDur requiredExtensions
      |requiredFeatures restart result rotate rx ry scale seed shapeRendering slope
      |spacing specularConstant specularExponent speed spreadMethod startOffset
      |stdDeviation stemh stemv stitchTiles stopColor stopOpacity
      |strikethroughPosition strikethroughThickness string stroke strokeDasharray
      |strokeDashoffset strokeLinecap strokeLinejoin strokeMiterlimit strokeOpacity
      |strokeWidth surfaceScale systemLanguage tableValues targetX targetY textAnchor
      |textDecoration textLength textRendering to transform u1 u2 underlinePosition
      |underlineThickness unicode unicodeBidi unicodeRange unitsPerEm vAlphabetic
      |vHanging vIdeographic vMathematical values vectorEffect version vertAdvY
      |vertOriginX vertOriginY viewBox viewTarget visibility widths wordSpacing
      |writingMode x x1 x2 xChannelSelector xHeight xlinkActuate xlinkArcrole
      |xlinkHref xlinkRole xlinkShow xlinkTitle xlinkType xmlns xmlnsXlink xmlBase
      |xmlLang xmlSpace y y1 y2 yChannelSelector z zoomAndPan"""
      .stripMargin.split('\n').flatMap(_.split(' ')).toSet ++ MDN.supportedAttributes

  def extract: (Seq[Tag], Seq[Attribute]) = {
    val allTags = supportedTags.map(t => Tag(t, "org.scalajs.dom.raw.HTMLElement", Seq.empty))

    val attributes = allAttributes.map(SVGToJSMapping.convert).flatMap { converted =>
      if (supportedAttributes.contains(converted.name)) {
        Some(Attribute(converted.name, converted.valueType, Seq.empty, None, false))
      } else None
    } ++ extraAttributes.map(e => Attribute(e._1.name, e._1.valueType, Seq.empty, None, false))

    (allTags, attributes)
  }
}
