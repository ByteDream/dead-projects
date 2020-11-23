package org.blueshard.olymp.utils;

public class SizeUnit {

    private int unit;
    private double size;

    private final static int BYTE = 1;
    private final static int KILOBYTE = 3;
    private final static int MEGABYTE = 5;
    private final static int GIGABYTE = 7;
    private final static int TERRABYTE = 9;
    private final static int PETABYTE = 11;
    private final static int EXABYTE = 13;

    SizeUnit(int unit, double size){
        this.unit = unit;
        this.size = size;
    }

    public static SizeUnit BYTES(double bytes){
        return new SizeUnit(BYTE, bytes);
    }

    public static SizeUnit KILOBYTE(double kilobytes) {
        return new SizeUnit(KILOBYTE, kilobytes);
    }

    public static SizeUnit MEGABYTE(double megabytes) {
        return new SizeUnit(MEGABYTE, megabytes);
    }

    public static SizeUnit GIGABYTE(double gigabytes) {
        return new SizeUnit(GIGABYTE, gigabytes);
    }

    public static SizeUnit TERRABYTE(double terrabytes) {
        return new SizeUnit(TERRABYTE, terrabytes);
    }

    public static SizeUnit PETABYTE(double petabytes) {
        return new SizeUnit(PETABYTE, petabytes);
    }

    public static SizeUnit EXABYTE(double exabytes) {
        return new SizeUnit(EXABYTE, exabytes);
    }

    public Double toByte(){
        switch (unit) {
            case BYTE:
                return size;
            case KILOBYTE:
                return size * 1000;
            case MEGABYTE:
                return size * 1000000;
            case GIGABYTE:
                return size * 1000000000;
            case TERRABYTE:
                return size * 1000000000000L;
            case PETABYTE:
                return size * 1000000000000000L;
            case EXABYTE:
                return size * 1000000000000000000L;
            default:
                return 0D;
        }
    }

    public Double toKilobyte(){
        switch (unit) {
            case BYTE:
                return size / 1000;
            case KILOBYTE:
                return size;
            case MEGABYTE:
                return size * 1000;
            case GIGABYTE:
                return size * 1000000;
            case TERRABYTE:
                return size * 1000000000;
            case PETABYTE:
                return size * 1000000000000L;
            case EXABYTE:
                return size * 1000000000000000L;
            default:
                return 0D;
        }
    }

    public Double toMegabyte(){
        switch (unit) {
            case BYTE:
                return size / 1000000;
            case KILOBYTE:
                return size / 1000;
            case MEGABYTE:
                return size;
            case GIGABYTE:
                return size * 1000;
            case TERRABYTE:
                return size * 1000000;
            case PETABYTE:
                return size * 1000000000;
            case EXABYTE:
                return size * 1000000000000L;
            default:
                return 0D;
        }
    }

    public Double toGigabyte(){
        switch (unit) {
            case BYTE:
                return size / 1000000000L;
            case KILOBYTE:
                return size / 1000000;
            case MEGABYTE:
                return size / 1000;
            case GIGABYTE:
                return size;
            case TERRABYTE:
                return size * 1000;
            case PETABYTE:
                return size * 1000000;
            case EXABYTE:
                return size * 1000000000;
            default:
                return 0D;
        }
    }

    public Double toTerrabyte(){
        switch (unit) {
            case BYTE:
                return size / 1000000000000L;
            case KILOBYTE:
                return size / 1000000000;
            case MEGABYTE:
                return size / 1000000;
            case GIGABYTE:
                return size / 1000;
            case TERRABYTE:
                return size;
            case PETABYTE:
                return size * 1000;
            case EXABYTE:
                return size * 1000000;
            default:
                return 0D;
        }
    }

    public Double toPetabyte(){
        switch (unit) {
            case BYTE:
                return size / 1000000000000000L;
            case KILOBYTE:
                return size / 1000000000000L;
            case MEGABYTE:
                return size / 1000000000;
            case GIGABYTE:
                return size / 1000000;
            case TERRABYTE:
                return size / 1000;
            case PETABYTE:
                return size;
            case EXABYTE:
                return size * 1000;
            default:
                return 0D;
        }
    }

    public Double toExabyte(){
        switch (unit) {
            case BYTE:
                return size / 1000000000000000000L;
            case KILOBYTE:
                return size / 1000000000000000L;
            case MEGABYTE:
                return size / 1000000000000L;
            case GIGABYTE:
                return size / 1000000000;
            case TERRABYTE:
                return size / 1000000;
            case PETABYTE:
                return size / 1000;
            case EXABYTE:
                return size;
            default:
                return 0D;
        }
    }

}
