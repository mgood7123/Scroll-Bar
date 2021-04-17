package smallville7123.AndroidDAW.SDK.UI.ScrollBar;

public class ChangeDetectors {
    private ChangeDetectors() {}

    public static class Short_Detector {
        private short value, value_old;
        public Short_Detector(short initialValue) { value = initialValue; value_old = initialValue;}
        public void set(short value) { value_old = this.value; this.value = value; }
        public short get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Int_Detector {
        private int value, value_old;
        public Int_Detector(int initialValue) { value = initialValue; value_old = initialValue;}
        public void set(int value) { value_old = this.value; this.value = value; }
        public int get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Long_Detector {
        private long value, value_old;
        public Long_Detector(long initialValue) { value = initialValue; value_old = initialValue;}
        public void set(long value) { value_old = this.value; this.value = value; }
        public long get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Float_Detector {
        private float value, value_old;
        public Float_Detector(float initialValue) { value = initialValue; value_old = initialValue;}
        public void set(float value) { value_old = this.value; this.value = value; }
        public float get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Double_Detector {
        private double value, value_old;
        public Double_Detector(double initialValue) { value = initialValue; value_old = initialValue;}
        public void set(double value) { value_old = this.value; this.value = value; }
        public double get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Boolean_Detector {
        private boolean value, value_old;
        public Boolean_Detector(boolean initialValue) { value = initialValue; value_old = initialValue;}
        public void set(boolean value) { value_old = this.value; this.value = value; }
        public boolean get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Char_Detector {
        private char value, value_old;
        public Char_Detector(char initialValue) { value = initialValue; value_old = initialValue;}
        public void set(char value) { value_old = this.value; this.value = value; }
        public char get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Byte_Detector {
        byte value, value_old;
        public Byte_Detector(byte initialValue) { value = initialValue; value_old = initialValue;}
        public void set(byte value) { value_old = this.value; this.value = value; }
        public byte get() { return value; }
        public boolean changed() { return value != value_old; }
    }

    public static class Object_Detector<T> {
        private T value, value_old;
        public Object_Detector(T initialValue) { value = initialValue; value_old = initialValue;}
        public void set(T value) { value_old = this.value; this.value = value; }
        public T get() { return value; }
        public boolean changed() { return value != value_old; }
    }

}
