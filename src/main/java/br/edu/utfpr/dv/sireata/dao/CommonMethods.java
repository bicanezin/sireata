package br.edu.utfpr.dv.sireata.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CommonMethods<M> {
    abstract public String buscarPorIdQuery();

    abstract public String listarPorAtaQuery(int idAta);

    abstract public String excluirQuery(int id);

    abstract public String listarTodosQuery(boolean apenasAtivos);

    abstract public String listarPorDepartamentoQuery(int idDepartamento);

    abstract public String listarParaCriacaoAtaQuery(int IdCampus, int idUsuario);

    abstract public String listarParaConsultaAtasQuery(int IdCampus, int idUsuario);

    public M buscar(int id, String query) throws SQLException{
        try (
                Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                return
                        rs.next() ?
                                this.carregarObjeto(rs)
                                : null;
            }

        }

    }

    public M buscarPorId(int id) throws SQLException {
        try (
                Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(buscarPorIdQuery())
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                return
                        rs.next() ?
                                this.carregarObjeto(rs)
                                : null;
            }
        }
    }

    public List<M> listarPorAta(int idAta) throws SQLException {
        try (
                Connection conn = ConnectionDAO.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(listarPorAtaQuery(idAta))
        ) {

            List<M> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    public abstract int inserir(M m) throws SQLException;

    public abstract int atualizar(M m) throws SQLException;

    public abstract int salvar(M m) throws SQLException;

    public void excluir(int id) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement()
                ) {
            stmt.execute(excluirQuery(id));
        }
    }

    abstract M carregarObjeto(ResultSet rs) throws SQLException;

    public List<M> listarTodos(boolean apenasAtivos) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(listarTodosQuery(apenasAtivos))
                ) {

            List<M> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    public List<M> listarPorDepartamento(int idDepartamento) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         listarPorDepartamentoQuery(idDepartamento))) {

            stmt.setInt(1, idDepartamento);
            ResultSet rs = stmt.executeQuery();

            List<M> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    public List<M> listarParaCriacaoAta(int idCampus, int idUsuario) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(listarParaCriacaoAtaQuery(idCampus, idUsuario))) {

            List<M> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    public List<M> listarParaConsultaAtas(int idCampus, int idUsuario) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery(listarParaConsultaAtasQuery(idCampus, idUsuario))) {

            List<M> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }
}