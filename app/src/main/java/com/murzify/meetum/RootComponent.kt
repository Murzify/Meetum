package com.murzify.meetum

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory

interface RootComponent

class RealRootComponent(
    componentContext: ComponentContext,
    componentFactory: ComponentFactory
) : RootComponent, ComponentContext by componentContext {

}