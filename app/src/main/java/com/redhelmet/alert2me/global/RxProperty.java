package com.redhelmet.alert2me.global;

import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import io.reactivex.subjects.BehaviorSubject;

public class RxProperty<T> extends ObservableField<T> {
    private BehaviorSubject<Optional<T>> subject = BehaviorSubject.create();

    public RxProperty(T value) {
        super(value);
        init();
    }

    public RxProperty() {
        super();
        init();
    }

    private void init() {
        subject.onNext(new Optional<>(get()));
        addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                T currentValue = get();
                subject.onNext(new Optional<>(currentValue));
            }
        });
    }

    public io.reactivex.Observable<T> asObservable() {
        return subject.filter(op -> op.value != null).map(op -> op.value);
    }

    private static class Optional<T> {
        public T value;

        private Optional(T value) {
            this.value = value;
        }
    }
}
