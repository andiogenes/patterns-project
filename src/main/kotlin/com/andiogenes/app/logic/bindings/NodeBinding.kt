package com.andiogenes.app.logic.bindings

import com.andiogenes.dataflow.Node
import com.andiogenes.effects.AudioEffectConstructor
import com.andiogenes.app.ui.components.FlowNode
import com.andiogenes.app.ui.components.Port
import com.andiogenes.app.ui.display.DisplayObject
import com.andiogenes.events.Event
import com.andiogenes.events.EventDispatcher

/**
 * Связывание модели и представления узла потока данных.
 */
class NodeBinding(val model: Node<AudioEffectConstructor>, val view: FlowNode) : EventDispatcher() {
    /**
     * Событие удаления привязанных данных.
     */
    data class NodeRemovedEvent(val model: Node<AudioEffectConstructor>, val view: FlowNode) : Event(nodeRemovedType)

    /**
     * Событие связи с одним из входных портов узла.
     */
    data class InEvent(
        val portIndex: Int,
        val model: Node<AudioEffectConstructor>,
        val view: FlowNode,
        val reason: Port.PortEvent.Reason
    ) : Event(inEventType)

    /**
     * Событие связи с одним из выходных портов узла.
     */
    data class OutEvent(
        val portIndex: Int,
        val model: Node<AudioEffectConstructor>,
        val view: FlowNode,
        val reason: Port.PortEvent.Reason
    ) : Event(outEventType)

    init {
        view.addEventListener(DisplayObject.disposeEventType) {
            dispatchEvent(NodeRemovedEvent(model, view))
        }
        view.addEventListener(FlowNode.inEventType) {
            if (it is FlowNode.InEvent) {
                dispatchEvent(InEvent(it.portIndex, model, view, it.portEvent.reason))
            }
        }
        view.addEventListener(FlowNode.outEventType) {
            if (it is FlowNode.OutEvent) {
                dispatchEvent(OutEvent(it.portIndex, model, view, it.portEvent.reason))
            }
        }
        view.addEventListener(FlowNode.valueChangedEventType) {
            if (it is FlowNode.ValueChangedEvent) {
                // Обновляем параметр модели по изменению в представлении.
                model.parameters[it.valueIndex] = it.newValue
            }
        }
    }

    companion object {
        /**
         * Ключ для подписки на [NodeRemovedEvent].
         */
        const val nodeRemovedType = "onBindingNodeRemoved"

        /**
         * Ключ для подписки на [InEvent].
         */
        const val inEventType = "onBindingInPort"

        /**
         * Ключ для подписка на [OutEvent].
         */
        const val outEventType = "onBindingOutPort"
    }
}