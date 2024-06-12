/*
    Gossamer, version [unreleased]. Copyright 2024 Jon Pretty, Propensive OÜ.

    The primary distribution site is: https://propensive.com/

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
    file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/

package gossamer

import rudiments.*
import anticipation.*

import language.experimental.captureChecking

trait Textual extends Presentational:
  type Self
  def classTag: ClassTag[Self]
  def length(text: Self): Int
  def text(text: Self): Text
  def map(text: Self, lambda: Char => Char): Self
  def range(text: Self, start: Int, end: Int): Self
  def empty: Self
  def concat(left: Self, right: Self): Self
  def unsafeChar(text: Self, index: Int): Char
  def indexOf(text: Self, sub: Text): Int

  extension (left: Self)
    @targetName("mul")
    infix def * (right: Int): Self =
      def recur(text: Self, n: Int, acc: Self): Self =
        if n <= 0 then acc else recur(text, n - 1, concat(acc, text))

      recur(left, right.max(0), empty)

    @targetName("add")
    infix def + (right: Self): Self = concat(left, right)

object Textual:
  def apply[TextType: Textual](text: Text): TextType = TextType(text)
