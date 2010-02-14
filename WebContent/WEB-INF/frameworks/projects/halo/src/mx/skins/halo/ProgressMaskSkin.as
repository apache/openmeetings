////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.Graphics;
import mx.skins.ProgrammaticSkin;

/**
 *  The skin for the mask of the ProgressBar's determinate and indeterminate bars.
 *  The mask defines the area in which the progress bar or 
 *  indeterminate progress bar is displayed.
 *  By default, the mask defines the progress bar to be inset 1 pixel from the track.
 *
 *  @see mx.controls.ProgressBar
 *  
 *  @langversion 3.0
 *  @playerversion Flash 9
 *  @playerversion AIR 1.1
 *  @productversion Flex 3
 */
public class ProgressMaskSkin extends ProgrammaticSkin
{
    include "../../core/Version.as";
        
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 9
     *  @playerversion AIR 1.1
     *  @productversion Flex 3
     */
    public function ProgressMaskSkin()
    {
        super();
    }

     //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */        
    override protected function updateDisplayList(w:Number, h:Number):void
    {
        super.updateDisplayList(w, h);

        // draw the mask
        var g:Graphics = graphics;
        g.clear();
        g.beginFill(0xFFFF00);
        g.drawRect(1, 1, w - 2, h - 2);
        g.endFill();
    }


}

}       