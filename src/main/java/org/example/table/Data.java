    package org.example.table;

    public class Data {

        private String name;
        private boolean flag;
        private int x;
        private int y;
        private int z;


        public Object[] toObjectArray() {
            Object[] array = new Object[6];
            array[0] = name;
            array[1] = flag;
            array[2] = x;
            array[3] = y;
            array[4] = z;
            array[5] = this;
            return array;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }
    }
