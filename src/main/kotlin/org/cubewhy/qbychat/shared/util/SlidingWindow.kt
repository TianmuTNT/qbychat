/*
 * Copyright (c) 2025. All rights reserved.
 *
 * This file is a part of the QbyChat project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.cubewhy.qbychat.shared.util

import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SlidingWindow(
    private val windowSize: Long = 64
) {
    private var baseSeq: Long = 0
    private val receivedSeqs = ConcurrentSkipListSet<Long>()
    private val lock = ReentrantLock()

    fun accept(sequence: Long): Boolean = lock.withLock {
        if (sequence < baseSeq) {
            return false
        }

        if (sequence >= baseSeq + windowSize) {
            return false
        }

        if (receivedSeqs.contains(sequence)) {
            return false
        }

        receivedSeqs.add(sequence)
        slideWindow()

        return true
    }

    private fun slideWindow() {
        while (receivedSeqs.contains(baseSeq)) {
            receivedSeqs.remove(baseSeq)
            baseSeq++
        }
    }
}