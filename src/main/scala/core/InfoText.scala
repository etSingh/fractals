package core

import sdl2.SDL
import sdl2.Extras._
import sdl2.SDL._
import sdl2.ttf.SDL_ttf._

import scala.scalanative.native._

class InfoText(fontColor: SDL_Color, width: Int, height: Int) {
  var numberOfLines = 0

  def updateLinesNumber(linesToDraw: CInt): Unit = numberOfLines = linesToDraw

  // Constructor OverLoad. If InfoText is instantiated without a color, fontColor is white.
  def this(width: Int, height: Int){
    this(SDL_Color(255.toUByte, 255.toUByte, 255.toUByte, 255.toUByte), width, height) // RGB color white: (255, 255, 255)
  }

  var isAnimationOn: String  = "off"

  def updateAnimationState(b: Boolean):       Unit = isAnimationOn = if(b) "On" else "Off"

  def draw(data: Data, font: Ptr[TTF_Font], renderer: Ptr[SDL.SDL_Renderer]): CInt = {
    // Prepare the texture with the text

    Zone { implicit z =>
      val w = stackalloc[CInt]
      val h = stackalloc[CInt]

      val nameAndDepthInfo : CString = toCString(s"${data.fractals(data.currentFractal).name} Depth: ${data.depth}")(z)

      val message = TTF_RenderText_Solid(font, nameAndDepthInfo, fontColor)
      val texture = SDL_CreateTextureFromSurface(renderer, message)

      SDL_QueryTexture(texture, null, null, w, h)
      var rect = stackalloc[SDL_Rect].init(10, 0, !w, !h)
      SDL_RenderCopy(renderer, texture, null, rect)

      val animationAndLinesInfo: CString = toCString(s"Animation: ${isAnimationOn}    Number of Lines: ${numberOfLines}")(z)

      val message2:              Ptr[SDL_Surface] = TTF_RenderText_Solid(font, animationAndLinesInfo, fontColor)
      var texture2:              Ptr[SDL_Texture] = SDL_CreateTextureFromSurface(renderer, message2)

      SDL_QueryTexture(texture2, null, null, w, h)
      rect = stackalloc[SDL_Rect].init(width - 10 - !w, 0, !w, !h) 
      SDL_RenderCopy(renderer, texture2, null, rect)
    }
  }
}
