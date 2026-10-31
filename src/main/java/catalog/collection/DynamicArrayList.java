package catalog.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Objects;
import java.util.RandomAccess;

/** A List backed by its own automatically growing array. */
public final class DynamicArrayList<E> extends AbstractList<E> implements RandomAccess {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    public DynamicArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative.");
        }
        elements = new Object[initialCapacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        Objects.checkIndex(index, size);
        return (E) elements[index];
    }

    @Override
    public boolean add(E element) {
        add(size, element);
        return true;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
        modCount++;
    }

    @Override
    public E set(int index, E element) {
        E previous = get(index);
        elements[index] = element;
        return previous;
    }

    @Override
    public E remove(int index) {
        E removed = get(index);
        int movedCount = size - index - 1;
        if (movedCount > 0) {
            System.arraycopy(elements, index + 1, elements, index, movedCount);
        }
        elements[--size] = null;
        modCount++;
        return removed;
    }

    @Override
    public void clear() {
        if (size > 0) {
            Arrays.fill(elements, 0, size, null);
            size = 0;
            modCount++;
        }
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity < 0) {
            throw new OutOfMemoryError("List is too large.");
        }
        if (requiredCapacity <= elements.length) {
            return;
        }
        long grownCapacity = (long) elements.length + elements.length / 2 + 1;
        int newCapacity = (int) Math.min(Integer.MAX_VALUE,
                Math.max(grownCapacity, requiredCapacity));
        elements = Arrays.copyOf(elements, newCapacity);
    }
}
