////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package spark.components
{

import flash.display.DisplayObject;
import flash.display.InteractiveObject;
import flash.events.ContextMenuEvent;
import flash.events.Event;
import flash.external.ExternalInterface;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.system.Capabilities;
import flash.ui.ContextMenu;
import flash.ui.ContextMenuItem;
import flash.utils.setInterval;
import mx.core.FlexGlobals;
import mx.core.Singleton;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.managers.FocusManager;
import mx.managers.ILayoutManager;
import mx.managers.ISystemManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched after the Application has been initialized,
 *  processed by the LayoutManager, and attached to the display list.
 * 
 *  @eventType mx.events.FlexEvent.APPLICATION_COMPLETE
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Event(name="applicationComplete", type="mx.events.FlexEvent")]

/**
 *  Dispatched when an error occurs anywhere in the application,
 *  such as when an HTTPService, WebService, or RemoteObject fails.
 * 
 *  @eventType flash.events.ErrorEvent.ERROR
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Event(name="error", type="flash.events.ErrorEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]
[Exclude(name="tabIndex", kind="property")]
[Exclude(name="toolTip", kind="property")]
[Exclude(name="x", kind="property")]
[Exclude(name="y", kind="property")]

//--------------------------------------
//  Other metadata
//--------------------------------------

/**
 *  The frameworks must be initialized by SystemManager.
 *  This factoryClass will be automatically subclassed by any
 *  MXML applications that don't explicitly specify a different
 *  factoryClass.
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Frame(factoryClass="mx.managers.SystemManager")]

[ResourceBundle("components")]

/**
 *  Flex defines a default, or Application, container that lets you start
 *  adding content to your application without explicitly defining
 *  another container.
 * 
 *  @mxml
 *
 *  <p>The <code>&lt;Application&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;Application
 *    <strong>Properties</strong>
 *    backgroundColor="0xFFFFFF"
 *    colorCorrection="default"
 *    frameRate="24"
 *    pageTitle""
 *    preloader="<i>No default</i>"
 *    preloaderBaseColor="<i>No default</i>"
 *    scriptRecursionLimit="1000"
 *    scriptTimeLimit="60"
 *    usePreloader="true"
 *    viewSourceURL=""
 *    xmlns:<i>No default</i>="<i>No default</i>"
 *  
 *    <strong>Events</strong>
 *    applicationComplete="<i>No default</i>"
 *    error="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class Application extends SkinnableContainer 
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function Application()
    {
        UIComponentGlobals.layoutManager = ILayoutManager(
            Singleton.getInstance("mx.managers::ILayoutManager"));
        UIComponentGlobals.layoutManager.usePhasedInstantiation = true;

        if (!FlexGlobals.topLevelApplication)
            FlexGlobals.topLevelApplication = this;

        super();
                    
        showInAutomationHierarchy = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var resizeHandlerAdded:Boolean = false;

    /**
     *  @private
     *  Placeholder for Preloader object reference.
     */
    private var preloadObj:Object;
    
    /**
     *  @private
     *  This flag indicates whether the width of the Application instance
     *  can change or has been explicitly set by the developer.
     *  When the stage is resized we use this flag to know whether the
     *  width of the Application should be modified.
     */
    private var resizeWidth:Boolean = true;
    
    /**
     *  @private
     *  This flag indicates whether the height of the Application instance
     *  can change or has been explicitly set by the developer.
     *  When the stage is resized we use this flag to know whether the
     *  height of the Application should be modified.
     */
    private var resizeHeight:Boolean = true;
    
    /**
     * @private
     * (Possibly null) reference to the View Source context menu item,
     * so that we can update it for runtime localization.
     */
    private var viewSourceCMI:ContextMenuItem;
    
    //----------------------------------
    //  backgroundColor
    //----------------------------------
    
    private var _backgroundColor:uint = 0xFFFFFF;
    
    [Inspectable(category="General", format="Color", defaultValue="0xFFFFFF")]
    [Bindable("backgroundColorUpdated")]
    
    /**
     *  Background color of a component. 
     *  This property specifies the background color, both while the application
     *  loads, and while it is running.
     *
     *  @default 0xFFFFFF
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get backgroundColor():uint
    {
        return _backgroundColor;
    }
    
    /**
     * @private
     */
    public function set backgroundColor(value:uint):void
    {
        if (value == _backgroundColor)
            return;
            
        _backgroundColor = value;
        
        dispatchEvent(new Event("backgroundColorUpdated"));
    }

    //----------------------------------
    //  colorCorrection
    //----------------------------------
    
    [Inspectable(enumeration="default,off,on", defaultValue="default" )]
    
   /**
    *  The value of the stage's <code>colorCorrection</code> property. 
    *  If this application does not have access to the stage's <code>colorCorrection</code> property, 
    *  the value of the <code>colorCorrection</code> property is <code>null</code>.
    *  
    *  <p>Only the main application is allowed to set the <code>colorCorrection</code>
    *  property. If a nested application's needs to set the color correction property, it 
    *  must set it by referencing the the main application's instance.</p>
    *
    *  @default ColorCorrection.DEFAULT
    *
    *  @see flash.display.ColorCorrection
    *  
    *  @langversion 3.0
    *  @playerversion Flash 10
    *  @playerversion AIR 1.5
    *  @productversion Flex 4
    */
    public function get colorCorrection():String
    {
        try
        {
            var sm:ISystemManager = systemManager;
            if (sm && sm.stage)
                return sm.stage.colorCorrection;
        }
        catch (e:SecurityError)
        {
            // ignore error if this application is not allow
            // to view the colorCorrection property.
        }

        return null;
    }
    
    /**
     * @private
     */
    public function set colorCorrection(value:String):void
    {
        // Since only the main application is allowed to change the value this property, there
        // is no need to catch security violations like in the getter.
        var sm:ISystemManager = systemManager;
        if (sm && sm.stage && sm.isTopLevelRoot())
            sm.stage.colorCorrection = value;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Compile-time pseudo-properties
    //
    //--------------------------------------------------------------------------

    // These declarations correspond to the MXML-compile-time attributes
    // allowed on the <Application> tag. These attributes affect the MXML
    // compiler, but they aren't actually used in the runtime framework.
    // The declarations appear here in order to provide metadata about these
    // attributes for FlexBuilder.

    //----------------------------------
    //  frameRate
    //----------------------------------

    [Inspectable(defaultValue="24")]

    /**
     *    Specifies the frame rate of the application.
     * 
     *    <p>Note: This property cannot be set by ActionScript code; it must be set in MXML code.</p>
     *
     *    @default 24
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var frameRate:Number;

    //----------------------------------
    //  pageTitle
    //----------------------------------

    /**
     *    Specifies a string that appears in the title bar of the browser.
     *    This property provides the same functionality as the
     *    HTML <code>&lt;title&gt;</code> tag.
     * 
     *    <p>Note: This property cannot be set by ActionScript code; it must be set in MXML code. 
     *    The value set in MXML code is designed to be used by a tool to update the HTML templates 
     *    provided with the SDK.</p>
     *
     *    @default ""
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var pageTitle:String;

    //----------------------------------
    //  preloader
    //----------------------------------

    [Inspectable(defaultValue="mx.preloaders.DownloadProgressBar")]

    /**
     *  Specifies the path of a SWC component class or ActionScript
     *  component class that defines a custom progress bar.
     *  A SWC component must be in the same directory as the MXML file
     *  or in the WEB-INF/flex/user_classes directory of your Flex
     *  web application.
     * 
     *  <p>Note: This property cannot be set by ActionScript code; it must be set in MXML code.</p>
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var preloader:Object;
    
    //----------------------------------
    //  preloaderBaseColor
    //----------------------------------
    
    [Inspectable(defaultValue="0xCCCCCC", format="Color")]
    
    /**
     *  Specifies the base color used by the default preloader component. This property
     *  has the same effect as the <code>baseColor</code> style used by Spark skins.
     *  Typically this property should be set to the same value as the 
     *  Application container's <code>baseColor</code> style property.
     *    
     *  <p>Note: This property cannot be set by ActionScript code; it must be set in MXML code.</p>
     * 
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    
    /* This property is not directly read by the download progress bar (preloader)
     * component. It is here so that it gets picked up by the compiler and included 
     * in the info() structure for the generated system manager. The download progress bar
     * grabs the value directly from the info() structure. */
    public var preloaderBaseColor:uint;

    //----------------------------------
    //  scriptRecursionLimit
    //----------------------------------

    [Inspectable(defaultValue="1000")]

    /**
     *    Specifies the maximum depth of Flash Player or AIR 
     *    call stack before the player stops.
     *    This is essentially the stack overflow limit.
     * 
     *    <p>Note: This property cannot be set by ActionScript code; it must be set in MXML code.</p>
     *
     *    @default 1000
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var scriptRecursionLimit:int;

    //----------------------------------
    //  scriptTimeLimit
    //----------------------------------

    [Inspectable(defaultValue="60")]

    /**
     *    Specifies the maximum duration, in seconds, that an ActionScript
     *    event handler can execute before Flash Player or AIR assumes
     *    that it is hung, and aborts it.
     *    The maximum allowable value that you can set is 60 seconds.
     *
     *  @default 60
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var scriptTimeLimit:Number;

    //----------------------------------
    //  usePreloader
    //----------------------------------

    [Inspectable(defaultValue="true")]

    /**
     *    If <code>true</code>, specifies to display the application preloader.
     * 
     *    <p>Note: This property cannot be set by ActionScript code; it must be set in MXML code.</p>
     *
     *    @default true
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var usePreloader:Boolean;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties (to block metadata from superclasses)
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  id
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get id():String
    {
        if (!super.id &&
            this == FlexGlobals.topLevelApplication && 
            ExternalInterface.available)
        {
            return ExternalInterface.objectID;
        }

        return super.id;
    }

    //----------------------------------
    //  percentHeight
    //----------------------------------

    /**
     *  @private
     */
    override public function set percentHeight(value:Number):void
    {
        super.percentHeight = value;

        invalidateDisplayList();
    }
    
    //----------------------------------
    //  percentWidth
    //----------------------------------

    /**
     *  @private
     */
    override public function set percentWidth(value:Number):void
    {
        super.percentWidth = value;

        invalidateDisplayList();
    }

    //----------------------------------
    //  tabIndex
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function set tabIndex(value:int):void
    {
    }

    //----------------------------------
    //  toolTip
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function set toolTip(value:String):void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  parameters
    //----------------------------------

    /**
     *  @private
     *  Storage for the parameters property.
     *  This variable is set in the initialize() method of SystemManager.
     */
    mx_internal var _parameters:Object;

    /**
     *  An Object containing name-value
     *  pairs representing the parameters provided to this Application.
     *
     *  <p>You can use a for-in loop to extract all the names and values
     *  from the parameters Object.</p>
     *
     *  <p>There are two sources of parameters: the query string of the
     *  Application's URL, and the value of the FlashVars HTML parameter
     *  (this affects only the main Application).</p>
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get parameters():Object
    {
        return _parameters;
    }

    //----------------------------------
    //  url
    //----------------------------------

    /**
     *  @private
     *  Storage for the url property.
     *  This variable is set in the initialize() method of SystemManager.
     */
    mx_internal var _url:String;

    /**
     *  The URL from which this Application's SWF file was loaded.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get url():String
    {
        return _url;
    }
    
    //----------------------------------
    //  viewSourceURL
    //----------------------------------

    /**
     *  @private
     *  Storage for viewSourceURL property.
     */
    private var _viewSourceURL:String;

    /**
     *  URL where the application's source can be viewed. Setting this
     *  property inserts a "View Source" menu item into the application's
     *  default context menu.  Selecting the menu item opens the
     *  <code>viewSourceURL</code> in a new window.
     *
     *  <p>You must set the <code>viewSourceURL</code> property 
     *  using MXML, not using ActionScript, as the following example shows:</p>
     *
     *  <pre>
     *    &lt;Application viewSourceURL="http://path/to/source"&gt;
     *      ...
     *    &lt;/Application&gt;</pre>
     *
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get viewSourceURL():String
    {
        return _viewSourceURL;
    }
    
    /**
     *  @private
     */
    public function set viewSourceURL(value:String):void
    {
        _viewSourceURL = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function initialize():void
    {
        // trace("FxApp initialize FxApp");
        
        var sm:ISystemManager = systemManager;
        
        _url = sm.loaderInfo.url;
        _parameters = sm.loaderInfo.parameters;

        initManagers(sm);

        // Setup the default context menu here. This allows the application
        // developer to override it in the initialize event, if desired.
        initContextMenu();

        super.initialize();
        
        // Stick a timer here so that we will execute script every 1.5s
        // no matter what.
        // This is strictly for the debugger to be able to halt.
        // Note: isDebugger is true only with a Debugger Player.
        if (sm.isTopLevel() && Capabilities.isDebugger == true)
            setInterval(debugTickler, 1500);
    }
    
    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        resizeWidth = isNaN(explicitWidth);
        resizeHeight = isNaN(explicitHeight);
        
        if (resizeWidth || resizeHeight)
        {
            resizeHandler(new Event(Event.RESIZE));

            if (!resizeHandlerAdded)
            {
                // weak reference
                systemManager.addEventListener(Event.RESIZE, resizeHandler, false, 0, true);
                resizeHandlerAdded = true;
            }
        }
        else
        {
            if (resizeHandlerAdded)
            {
                systemManager.removeEventListener(Event.RESIZE, resizeHandler);
                resizeHandlerAdded = false;
            }
        }
    }
    
    /**
     *  @private
     */
    override protected function resourcesChanged():void
    {
        super.resourcesChanged();
        
        // "View Source" on the context menu
        if (viewSourceCMI)
        {
            viewSourceCMI.caption = resourceManager.getString("components", "viewSource");
        }
    }
    
    //----------------------------------
    //  unscaledHeight
    //----------------------------------
    
    /**
     *  @private
     */
    override mx_internal function setUnscaledHeight(value:Number):void
    {
        // we invalidate so we can properly add/remove the resize 
        // event handler (SDK-12664)
        invalidateProperties();
        
        super.mx_internal::setUnscaledHeight(value);
    }
    
    //----------------------------------
    //  unscaledWidth
    //----------------------------------
    
    /**
     *  @private
     */
    override mx_internal function setUnscaledWidth(value:Number):void
    {
        // we invalidate so we can properly add/remove the resize 
        // event handler (SDK-12664)
        invalidateProperties();
        
        super.mx_internal::setUnscaledWidth(value);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This is here so we get the this pointer set to Application.
     */
    private function debugTickler():void
    {
        // We need some bytes of code in order to have a place to break.
        var i:int = 0;
    }

    /**
     *  @private
     */
    private function initManagers(sm:ISystemManager):void
    {
        if (sm.isTopLevel())
        {
            focusManager = new FocusManager(this);
            sm.activate(this);
        }
    }
    
    /**
     *  @private
     *  Disable all the built-in items except "Print...".
     */
    private function initContextMenu():void
    {
        // context menu already set
        // nothing to init
        if (flexContextMenu != null)
        {
            // make sure we set it back on systemManager b/c it may have been overriden by now
            if (systemManager is InteractiveObject)
                InteractiveObject(systemManager).contextMenu = contextMenu;
            return;
        }
        
        var defaultMenu:ContextMenu = new ContextMenu();
        defaultMenu.hideBuiltInItems();
        defaultMenu.builtInItems.print = true;

        if (_viewSourceURL)
        {
            // don't worry! this gets updated in resourcesChanged()
            const caption:String = resourceManager.getString("components", "viewSource");
            
            viewSourceCMI = new ContextMenuItem(caption, true);
            viewSourceCMI.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, menuItemSelectHandler);
            
            defaultMenu.customItems.push(viewSourceCMI);
        }

        contextMenu = defaultMenu;
        
        if (systemManager is InteractiveObject)
            InteractiveObject(systemManager).contextMenu = defaultMenu;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private 
     *  Triggered by a resize event of the stage.
     *  Sets the new width and height.
     *  After the SystemManager performs its function,
     *  it is only necessary to notify the children of the change.
     */
    private function resizeHandler(event:Event):void
    {
        // When user has not specified any width/height,
        // application assumes the size of the stage.
        // If developer has specified width/height,
        // the application will not resize.
        // If developer has specified percent width/height,
        // application will resize to the required value
        // based on the current stage width/height.
        // If developer has specified min/max values,
        // then application will not resize beyond those values.

        var w:Number;
        var h:Number
        
        if (resizeWidth)
        {
            if (isNaN(percentWidth))
            {
                w = DisplayObject(systemManager).width;
            }
            else 
            {
                super.percentWidth = Math.max(percentWidth, 0);
                super.percentWidth = Math.min(percentWidth, 100);
                w = percentWidth*screen.width/100;
            }

            if (!isNaN(explicitMaxWidth))
                w = Math.min(w, explicitMaxWidth);

            if (!isNaN(explicitMinWidth))
                w = Math.max(w, explicitMinWidth);
        }
        else
        {
            w = width;
        }
        
        if (resizeHeight)
        {
            if (isNaN(percentHeight))
            {
                h = DisplayObject(systemManager).height;
            }
            else
            {
                super.percentHeight = Math.max(percentHeight, 0);
                super.percentHeight = Math.min(percentHeight, 100);
                h = percentHeight*screen.height/100;
            }
            
            if (!isNaN(explicitMaxHeight))
                h = Math.min(h, explicitMaxHeight);

            if (!isNaN(explicitMinHeight))
                h = Math.max(h, explicitMinHeight);
        }
        else
        {
            h = height;
        }
        
        if (w != width || h != height)
        {
            invalidateProperties();
            invalidateSize();
        }

        setActualSize(w, h);

        invalidateDisplayList();
    }
    
    /**
     *  @private
     *  Called when the "View Source" item in the application's context menu is
     *  selected.
     */
    protected function menuItemSelectHandler(event:Event):void
    {
        navigateToURL(new URLRequest(_viewSourceURL), "_blank");
    }
}

}
