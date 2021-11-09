package vivid.money.elmslie.core.util

import java.util.Collections.newSetFromMap
import java.util.concurrent.ConcurrentHashMap

internal class ConcurrentHashSet<T> : MutableSet<T> by newSetFromMap(ConcurrentHashMap())
