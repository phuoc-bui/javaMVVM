/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.phuocbui.basemodule.global;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
public class Event<T> {
    private T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Returns the content and prevents its use again.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    public T peekContent() {
        return content;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    /**
     * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
     * already been handled.
     * <p>
     * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
     */
    public static class EventObserver<T> implements Observer<Event<T>> {

        private OnEventUnhandledContent<T> listener;

        public EventObserver(OnEventUnhandledContent<T> listener) {
            this.listener = listener;
        }

        @Override
        public void onChanged(@Nullable Event<T> tEvent) {
            if (tEvent == null) return;
            T content = tEvent.getContentIfNotHandled();
            if (content != null) {
                listener.onEventUnhandled(content);
            }
        }
    }

    public interface OnEventUnhandledContent<T> {
        void onEventUnhandled(T value);
    }
}
