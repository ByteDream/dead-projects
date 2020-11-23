package org.blueshard.sekaijuclt.utils;

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
        return switch (unit) {
            case BYTE -> size;
            case KILOBYTE -> size * 1000;
            case MEGABYTE -> size * 1000000;
            case GIGABYTE -> size * 1000000000;
            case TERRABYTE -> size * 1000000000000L;
            case PETABYTE -> size * 1000000000000000L;
            case EXABYTE -> size * 1000000000000000000L;
            default -> 0D;
        };
    }

    public Double toKilobyte(){
        return switch (unit) {
            case BYTE -> size / 1000;
            case KILOBYTE -> size;
            case MEGABYTE -> size * 1000;
            case GIGABYTE -> size * 1000000;
            case TERRABYTE -> size * 1000000000;
            case PETABYTE -> size * 1000000000000L;
            case EXABYTE -> size * 1000000000000000L;
            default -> 0D;
        };
    }

    public Double toMegabyte(){
        return switch (unit) {
            case BYTE -> size / 1000000;
            case KILOBYTE -> size / 1000;
            case MEGABYTE -> size;
            case GIGABYTE -> size * 1000;
            case TERRABYTE -> size * 1000000;
            case PETABYTE -> size * 1000000000;
            case EXABYTE -> size * 1000000000000L;
            default -> 0D;
        };
    }

    public Double toGigabyte(){
        return switch (unit) {
            case BYTE -> size / 1000000000L;
            case KILOBYTE -> size / 1000000;
            case MEGABYTE -> size / 1000;
            case GIGABYTE -> size;
            case TERRABYTE -> size * 1000;
            case PETABYTE -> size * 1000000;
            case EXABYTE -> size * 1000000000;
            default -> 0D;
        };
    }

    public Double toTerrabyte(){
        return switch (unit) {
            case BYTE -> size / 1000000000000L;
            case KILOBYTE -> size / 1000000000;
            case MEGABYTE -> size / 1000000;
            case GIGABYTE -> size / 1000;
            case TERRABYTE -> size;
            case PETABYTE -> size * 1000;
            case EXABYTE -> size * 1000000;
            default -> 0D;
        };
    }

    public Double toPetabyte(){
        return switch (unit) {
            case BYTE -> size / 1000000000000000L;
            case KILOBYTE -> size / 1000000000000L;
            case MEGABYTE -> size / 1000000000;
            case GIGABYTE -> size / 1000000;
            case TERRABYTE -> size / 1000;
            case PETABYTE -> size;
            case EXABYTE -> size * 1000;
            default -> 0D;
        };
    }

    public Double toExabyte(){
        return switch (unit) {
            case BYTE -> size / 1000000000000000000L;
            case KILOBYTE -> size / 1000000000000000L;
            case MEGABYTE -> size / 1000000000000L;
            case GIGABYTE -> size / 1000000000;
            case TERRABYTE -> size / 1000000;
            case PETABYTE -> size / 1000;
            case EXABYTE -> size;
            default -> 0D;
        };
    }

}
