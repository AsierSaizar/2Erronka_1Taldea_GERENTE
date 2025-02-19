package com.example.gerenteapp;

public class Mahaia {
        private int id;
        private int mahaila_zenbakia;
        private int eserlekuak;
        private int habilitado;
        private String updated_at;


        public String getUpdated_at() {
                return updated_at;
        }

        public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
        }

        public int getHabilitado() {
                return habilitado;
        }

        public Mahaia(int id, int mahaila_zenbakia, int eserlekuak, int habilitado, String update_at) {
                this.id = id;
                this.mahaila_zenbakia = mahaila_zenbakia;
                this.eserlekuak = eserlekuak;
                this.habilitado = habilitado;
                this.updated_at = update_at;
        }


        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getMahaila_zenbakia() {
                return mahaila_zenbakia;
        }

        public void setMahaila_zenbakia(int mahaila_zenbakia) {
                this.mahaila_zenbakia = mahaila_zenbakia;
        }

        public int getEserlekuak() {
                return eserlekuak;
        }

        public void setEserlekuak(int eserlekuak) {
                this.eserlekuak = eserlekuak;
        }

        public int isHabilitado() {
                return habilitado;
        }

        public void setHabilitado(int habilitado) {
                this.habilitado = habilitado;
        }

        @Override
        public String toString() {
                return "Mahaila{" +
                        "id=" + id +
                        ", mahaila_zenbakia=" + mahaila_zenbakia +
                        ", eserlekuak=" + eserlekuak +
                        ", habilitado=" + habilitado +
                        '}';
        }
}
