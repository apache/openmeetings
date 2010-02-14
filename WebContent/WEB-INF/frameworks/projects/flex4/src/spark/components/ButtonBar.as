////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package spark.components
{
import flash.display.InteractiveObject;
import flash.events.Event;
import flash.events.EventPhase;
import flash.events.IEventDispatcher;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;

import spark.components.supportClasses.ListBase;
import spark.events.RendererExistenceEvent;

import mx.collections.IList;
import mx.core.EventPriority;
import mx.core.IFactory;
import mx.core.IVisualElement;
import mx.core.mx_internal;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.ItemClickEvent;
import mx.managers.IFocusManagerComponent;

[IconFile("ButtonBar.png")]

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when a navigation item is selected.
 *
 *  @eventType mx.events.ItemClickEvent.ITEM_CLICK
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Event(name="itemClick", type="mx.events.ItemClickEvent")]

/**
 *  The ButtonBar control displays a set of Buttons 
 *
 *  @includeExample examples/ButtonBarExample.mxml
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class ButtonBar extends ListBase implements IFocusManagerComponent 
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constants
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
    public function ButtonBar()
    {
        super();
        itemRendererFunction = defaultButtonBarItemRendererFunction;
        
        //Add a keyDown event listener so we can adjust
        //selection accordingly.  
        addEventListener(KeyboardEvent.KEY_DOWN, buttonBar_keyDownHandler);
        addEventListener(KeyboardEvent.KEY_UP, buttonBar_keyUpHandler);

        tabChildren = false;
        tabEnabled = true;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Index of currently focused child.
     */
    private var focusedIndex:int = 0;

    /**
     *  @private
     */
    private var inKeyUpHandler:Boolean;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  arrowKeysWrapFocus
    //---------------------------------- 
    
    /**
     * If true, using arrow keys to navigate within
     * the ButtonBar will wrap around when it hits
     * either end.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var arrowKeysWrapFocus:Boolean;

    //----------------------------------
    //  firstButton
    //---------------------------------- 
    
    [SkinPart(required="false", type="mx.core.IVisualElement")]
    
    /**
     * A skin part that defines the first button.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var firstButton:IFactory;
    
    //----------------------------------
    //  lastButton
    //---------------------------------- 
    
    [SkinPart(required="false", type="mx.core.IVisualElement")]
    
    /**
     * A skin part that defines the last button.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var lastButton:IFactory;

    //----------------------------------
    //  middleButton
    //---------------------------------- 
    
    [SkinPart(required="true", type="mx.core.IVisualElement")]
    
    /**
     * A skin part that defines the middle button(s).
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var middleButton:IFactory;

    
    //--------------------------------------------------------------------------
    //
    //  Overridden Properties
    //
    //--------------------------------------------------------------------------

    private var requiresSelectionChanging:Boolean;
    
    //----------------------------------
    //  requiresSelection
    //---------------------------------- 
    
    /**
     *  @private
     */
    override public function set requiresSelection(value:Boolean):void
    {
        super.requiresSelection = value;
        requiresSelectionChanging = true;
    }

    //----------------------------------
    //  dataProvider
    //----------------------------------
     
    /**
     *  @private
     */    
    override public function set dataProvider(value:IList):void
    {
        if (dataProvider)
            dataProvider.removeEventListener(CollectionEvent.COLLECTION_CHANGE, resetCollectionChangeHandler);
    
        // not really a default handler, we just want it to run after the datagroup
        if (value)
            value.addEventListener(CollectionEvent.COLLECTION_CHANGE, resetCollectionChangeHandler, false, EventPriority.DEFAULT_HANDLER);

        super.dataProvider = value;
    }

    /**
     *  @private
     */
    private function resetCollectionChangeHandler(event:Event):void
    {
        if (event is CollectionEvent)
        {
            var ce:CollectionEvent = CollectionEvent(event);

            if (ce.kind == CollectionEventKind.ADD || 
                ce.kind == CollectionEventKind.REMOVE)
            {
                // force reset here so first/middle/last skins
                // get reassigned
                ce = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
                ce.kind = CollectionEventKind.RESET;
                dataProvider.dispatchEvent(ce);
            }
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (requiresSelectionChanging && dataProvider)
        {
            requiresSelectionChanging = false;
            var n:int = dataProvider.length;
            for (var i:int = 0; i < n; i++)
            {
                var renderer:IItemRenderer = 
                    dataGroup.getElementAt(i) as IItemRenderer;
                if (renderer)
                    renderer.allowDeselection = !requiresSelection;
            }
        }
    }
    
    /**
     *  @private
     */
    override public function drawFocus(isFocused:Boolean):void
    {
        adjustLayering(focusedIndex);
        drawButtonFocus(focusedIndex, isFocused);
    }


    /**
     *  @private
     */
    override protected function itemSelected(index:int, selected:Boolean):void
    {
        super.itemSelected(index, selected);
        
        var renderer:IItemRenderer = 
            dataGroup.getElementAt(index) as IItemRenderer;
        
        if (renderer)
        {
            focusedIndex = index;
            renderer.selected = selected;
        }
    }
        
    /**
     *  @inheritDoc
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    override protected function partAdded(partName:String, instance:Object):void
    {
        super.partAdded(partName, instance);
        if (instance == dataGroup)
        {
            dataGroup.addEventListener(
                RendererExistenceEvent.RENDERER_ADD, dataGroup_rendererAddHandler);
            dataGroup.addEventListener(
                RendererExistenceEvent.RENDERER_REMOVE, dataGroup_rendererRemoveHandler);
        }
    }

    /**
     *  @inheritDoc
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    override protected function partRemoved(partName:String, instance:Object):void
    {
        if (instance == dataGroup)
        {
            dataGroup.removeEventListener(
                RendererExistenceEvent.RENDERER_ADD, dataGroup_rendererAddHandler);
            dataGroup.removeEventListener(
                RendererExistenceEvent.RENDERER_REMOVE, dataGroup_rendererRemoveHandler);
        }
        
        super.partRemoved(partName, instance);
    }

    //--------------------------------------------------------------------------
    //
    //  Private Methods
    //
    //--------------------------------------------------------------------------

    private function defaultButtonBarItemRendererFunction(data:Object):IFactory
    {
        var i:int = dataProvider.getItemIndex(data);
        if (i == 0)
            return firstButton ? firstButton : middleButton;

        var n:int = dataProvider.length - 1;
        if (i == n)
            return lastButton ? lastButton : middleButton;

        return middleButton;
    }

    
    //--------------------------------------------------------------------------
    //
    //  Event Handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Called when an item has been added to this component.
     */
    private function dataGroup_rendererAddHandler(event:RendererExistenceEvent):void
    {
        var renderer:IVisualElement = event.renderer; 
        var index:int = event.index;
        
        if (renderer)
        {
            renderer.addEventListener("click", item_clickHandler);
            if (renderer is IFocusManagerComponent)
                IFocusManagerComponent(renderer).focusEnabled = false;
            if (renderer is IItemRenderer)
                IItemRenderer(renderer).allowDeselection = !requiresSelection;
            updateRenderer(renderer);
        }
    }
    
    /**
     *  @private
     *  Called when an item has been removed from this component.
     */
    private function dataGroup_rendererRemoveHandler(event:RendererExistenceEvent):void
    {        
        var renderer:IVisualElement = event.renderer;
        
        if (renderer)
            renderer.removeEventListener("click", item_clickHandler);
    }
    
    /**
     *  @private
     *  Called when an item is clicked.
     */
    private function item_clickHandler(event:MouseEvent):void
    {
        var index:int = dataGroup.getElementIndex(
                            event.currentTarget as IVisualElement);

        var currentRenderer:IItemRenderer;
        if (focusedIndex >= 0  && !inKeyUpHandler)
        {
            currentRenderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
            currentRenderer.caret = false;
        }

        if (index == selectedIndex)
        {
            if (!requiresSelection)
                selectedIndex = -1;
        }
        else
        {
            focusedIndex = selectedIndex = index;
        }

        var newEvent:ItemClickEvent =
            new ItemClickEvent(ItemClickEvent.ITEM_CLICK);
        if ("label" in event.currentTarget)
            newEvent.label = event.currentTarget.label;
        newEvent.index = index;
        newEvent.relatedObject = InteractiveObject(event.currentTarget);
        newEvent.item = dataProvider ?
                        dataProvider.getItemAt(index) :
                        null;
        dispatchEvent(newEvent);
    }
    
    /**
     *  @private
     *  Attempt to lift the focused button above the others
     *  so that the focus ring can show.
     */
    private function adjustLayering(focusedIndex:int):void
    {
        var n:int = dataProvider ? dataProvider.length : 0;
        for (var i:int = 0; i < n; i++)
        {
            var renderer:IVisualElement = IVisualElement(dataGroup.getElementAt(i));
            if (i == focusedIndex)
                renderer.layer = 1;
            else
                renderer.layer = 0;
        }
    }

    /**
     *  @private
     */
    private function buttonBar_keyDownHandler(event:KeyboardEvent):void
    {
        var currentRenderer:IItemRenderer;
        var renderer:IItemRenderer;
        
        if (event.eventPhase == EventPhase.BUBBLING_PHASE)
            return;

        if (!enabled)
            return;

        if (!dataProvider)
            return;

        var length:int = dataProvider.length;
        switch (event.keyCode)
        {
            case Keyboard.UP:
            case Keyboard.LEFT:
            {
                currentRenderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
                if (focusedIndex > 0 || arrowKeysWrapFocus)
                {
                    if (currentRenderer)
                        currentRenderer.caret = false;
                    focusedIndex = (focusedIndex - 1 + length) % length;
                    adjustLayering(focusedIndex);
                    renderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
                    if (renderer)
                        renderer.caret = true;
                }

                event.stopPropagation();
                break;
            }
            case Keyboard.DOWN:
            case Keyboard.RIGHT:
            {
                currentRenderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
                if (focusedIndex < dataProvider.length - 1 || arrowKeysWrapFocus)
                {
                    if (currentRenderer)
                        currentRenderer.caret = false;
                    focusedIndex = (focusedIndex + 1) % length;
                    adjustLayering(focusedIndex);
                    renderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
                    if (renderer)
                        renderer.caret = true;
                }

                event.stopPropagation();
                break;
            }            
            case Keyboard.SPACE:
            {
                currentRenderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
                if (!currentRenderer || (currentRenderer.selected && requiresSelection))
                    return;
                currentRenderer.dispatchEvent(event);
                break;
            }            
        }
    }
  
    /**
     *  @private
     */
    private function buttonBar_keyUpHandler(event:KeyboardEvent):void
    {
        var currentRenderer:IItemRenderer;
        var renderer:IItemRenderer;

        if (event.eventPhase == EventPhase.BUBBLING_PHASE)
            return;

        if (!enabled)
            return;

        inKeyUpHandler = true;

        switch (event.keyCode)
        {
            case Keyboard.SPACE:
            {
                  currentRenderer = dataGroup.getElementAt(focusedIndex) as IItemRenderer;
                if (!currentRenderer || (currentRenderer.selected && requiresSelection))
                    return;
                currentRenderer.dispatchEvent(event);
                break;
            }            
        }

        inKeyUpHandler = false;
    }

    /**
     *  @private
     */
    private function drawButtonFocus(index:int, focused:Boolean):void
    {
        var n:int = dataProvider ? dataProvider.length : 0;
        if (n > 0 && index < n)
        {
            var renderer:IItemRenderer = 
                dataGroup.getElementAt(index) as IItemRenderer;
            if (renderer)
                renderer.caret = focused;
        }
    }
}

}

