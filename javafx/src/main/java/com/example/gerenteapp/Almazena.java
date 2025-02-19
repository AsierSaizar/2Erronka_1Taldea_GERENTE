package com.example.gerenteapp;

public class Almazena {
    private int id;
    private String izena;
    private String mota;
    private String ezaugarria;
    private int stock;
    private String unitatea;
    private int min;
    private int max;
    private String createdAt;
    private int createdBy;

    // Constructor
    public Almazena(int id, String izena, String mota, String ezaugarria, int stock, String unitatea, int min, int max, String createdAt, int createdBy) {
        this.id = id;
        this.izena = izena;
        this.mota = mota;
        this.ezaugarria = ezaugarria;
        this.stock = stock;
        this.unitatea = unitatea;
        this.min = min;
        this.max = max;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIzena() {
        return izena;
    }

    public void setIzena(String izena) {
        this.izena = izena;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getEzaugarria() {
        return ezaugarria;
    }

    public void setEzaugarria(String ezaugarria) {
        this.ezaugarria = ezaugarria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnitatea() {
        return unitatea;
    }

    public void setUnitatea(String unitatea) {
        this.unitatea = unitatea;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Almazena{" +
                "id=" + id +
                ", izena='" + izena + '\'' +
                ", mota='" + mota + '\'' +
                ", ezaugarria='" + ezaugarria + '\'' +
                ", stock=" + stock +
                ", unitatea='" + unitatea + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", createdAt='" + createdAt + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
