////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.Graphics;
import mx.core.mx_internal;
import mx.skins.ProgrammaticSkin;

use namespace mx_internal;

/**
 *  The skins of the DateChooser's indicators for 
 *  displaying today, rollover and selected dates.
 *  
 *  @langversion 3.0
 *  @playerversion Flash 9
 *  @playerversion AIR 1.1
 *  @productversion Flex 3
 */
public class DateChooserIndicator extends ProgrammaticSkin
{
    include "../../core/Version.as";    
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor	 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 9
     *  @playerversion AIR 1.1
     *  @productversion Flex 3
     */
    public function DateChooserIndicator()
    {
        super();             
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var indicatorColor:String = "rollOverColor";    

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
		
		var g:Graphics = graphics;
		
		g.clear();
        g.lineStyle(0, getStyle("themeColor"), 0)
        g.beginFill(getStyle(mx_internal::indicatorColor));
        g.drawRect(1, 0, w - 2, h);
        g.endFill();
	}
}

}
