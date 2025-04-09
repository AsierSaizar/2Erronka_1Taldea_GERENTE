package com.example.gerenteapp;

import org.junit.jupiter.api.Test;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class LangileaKudeatzaileaTest {

    @Test
    void insertLangilea() {
        System.setProperty("test.env", "true");
        Langilea l = new Langilea(0, "Test", "User", "pass", "test@correo.com", 1, null, 1);
        assertTrue(LangileaKudeatzailea.insertLangilea(l));

        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE email = ?")) {
            stmt.setString(1, l.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error limpiando: " + e.getMessage());
        }
    }

    @Test
    void editLangilea() {
        System.setProperty("test.env", "true");
        String email = "edit@test.com";

        // Asegurarse de que el email no exista antes de insertar
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error limpiando antes de testear: " + e.getMessage());
        }

        // Insertar nuevo langilea
        Langilea l = new Langilea(0, "Ori", "Apellido", "pass", email, 1, null, 1);
        assertTrue(LangileaKudeatzailea.insertLangilea(l));

        // Obtener ID insertado
        int id = getIdByEmail(email);

        // Editar langilea existente
        Langilea editado = new Langilea(id, "Edit", "ApellidoEditado", "newpass", email, 2, null, 0);
        assertTrue(LangileaKudeatzailea.editLangilea(editado));

        // Limpiar después del test
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error limpiando después del test: " + e.getMessage());
        }
    }


    @Test
    void deleteLangilea() {
        System.setProperty("test.env", "true");

        String email = "delete@correo.com";

        // Limpiar antes de insertar (evitar duplicados)
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error limpiando antes del test: " + e.getMessage());
        }

        // Insertar un nuevo langilea
        Langilea l = new Langilea(0, "Carlos", "Test", "clave", email, 1, null, 1);
        assertTrue(LangileaKudeatzailea.insertLangilea(l), "Error al insertar langilea");

        // Obtener el ID del langilea insertado
        String id = "";
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getString("id");
            }
        } catch (SQLException e) {
            fail("Error al obtener el ID del langilea: " + e.getMessage());
        }

        // Asegurarnos de que se obtuvo el ID
        assertFalse(id.isEmpty(), "El ID del langilea no se obtuvo correctamente");

        // Borrar el langilea por ID
        assertTrue(LangileaKudeatzailea.deleteLangilea(id), "El langilea no pudo ser marcado como eliminado");

        // Verificar que el langilea se haya eliminado (su campo `deleted_at` debe estar actualizado)
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("SELECT deleted_at FROM langilea WHERE id = ?")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String deletedAt = rs.getString("deleted_at");
                assertNotNull(deletedAt, "El langilea no fue marcado como eliminado, `deleted_at` es null");
            } else {
                fail("No se encontró el langilea con ID: " + id);
            }
        } catch (SQLException e) {
            fail("Error al verificar la eliminación del langilea: " + e.getMessage());
        }

        // Limpiar después del test
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error limpiando después del test: " + e.getMessage());
        }
    }



    @Test
    void testBerreskuratuLangileaPorId() {
        System.setProperty("test.env", "true");

        String email = "recover@test.com";

        // Eliminar langilea si ya existe
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar antes del insert: " + e.getMessage());
        }

        Langilea l = new Langilea(0, "Recover", "User", "rec123", email, 1, null, 1);
        assertTrue(LangileaKudeatzailea.insertLangilea(l), "Fallo al insertar langilea");

        int id = -1;

        // Obtener ID
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            } else {
                fail("Langilea no encontrado tras insertarlo");
            }
        } catch (SQLException e) {
            fail("Error al obtener ID: " + e.getMessage());
        }

        // Marcar como eliminado
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("UPDATE langilea SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error al simular eliminación: " + e.getMessage());
        }

        // Restaurar
        assertTrue(LangileaKudeatzailea.berreskuratuLangilea(String.valueOf(id)), "El langilea no se pudo recuperar");

        // Limpiar
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM langilea WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar después del test: " + e.getMessage());
        }
    }






    // Método auxiliar
    private int getIdByEmail(String email) {
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM langilea WHERE email = ?")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) {
            fail("Error obteniendo ID: " + e.getMessage());
        }
        return -1;
    }
}
