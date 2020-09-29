package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends CommonMethods<Usuario> {
    public String buscarPorIdQuery() {
        return ("SELECT * FROM usuarios WHERE idUsuario = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return null;
    }

    public String excluirQuery(int id) {
        return null;
    }

    public String listarTodosQuery(boolean apenasAtivos) {
        return ("SELECT * FROM usuarios WHERE login <> 'admin' " + (apenasAtivos ? " AND ativo = 1 " : "") + " ORDER BY nome");
    }

    public String listarPorDepartamentoQuery(int idDepartamento) {
        return null;
    }

    public String listarParaCriacaoAtaQuery(int idDepartamento, int idUsuario) {
        return null;
    }

    public String listarParaConsultaAtasQuery(int idDepartamento, int idUsuario) {
        return null;
    }

    public Usuario buscarPorLogin(String login) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE login = ?")) {

            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();

            return rs.next() ? this.carregarObjeto(rs) : null;
        }
    }

    public String buscarEmail(int id) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT email FROM usuarios WHERE idUsuario = ?")) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getString("email") : "";
        }
    }

    public List<Usuario> listar(String nome, boolean apenasAtivos, boolean apenasExternos) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login <> 'admin' " +
                (!nome.isEmpty() ? " AND nome LIKE ? " : "") +
                (apenasAtivos ? " AND ativo = 1 " : "") +
                (apenasExternos ? " AND externo = 1 " : "") +
                "ORDER BY nome";

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)
                ) {
            if (!nome.isEmpty()) {
                stmt.setString(1, "%" + nome + "%");
            }

            ResultSet rs = stmt.executeQuery();
            List<Usuario> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    @Override
    public int inserir(Usuario usuario) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios(nome, login, senha, email, externo, ativo, administrador) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.isExterno() ? 1 : 0);
            stmt.setInt(6, usuario.isAtivo() ? 1 : 0);
            stmt.setInt(7, usuario.isAdministrador() ? 1 : 0);

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) usuario.setIdUsuario(rs.getInt(1));
            }
            return usuario.getIdUsuario();
        }
    }

    @Override
    public int atualizar(Usuario usuario) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE usuarios SET nome=?, login=?, senha=?, email=?, externo=?, ativo=?, administrador=? WHERE idUsuario=?")
        ) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.isExterno() ? 1 : 0);
            stmt.setInt(6, usuario.isAtivo() ? 1 : 0);
            stmt.setInt(7, usuario.isAdministrador() ? 1 : 0);

            stmt.setInt(8, usuario.getIdUsuario());
            stmt.execute();

            return usuario.getIdUsuario();
        }
    }

    @Override
    public int salvar(Usuario usuario) throws SQLException {
        boolean inserir = (usuario.getIdUsuario() == 0);

        return inserir ? inserir(usuario) : atualizar(usuario);
    }

    @Override
    Usuario carregarObjeto(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setEmail(rs.getString("email"));
        usuario.setExterno(rs.getInt("externo") == 1);
        usuario.setAtivo(rs.getInt("ativo") == 1);
        usuario.setAdministrador(rs.getInt("administrador") == 1);

        return usuario;
    }

    public String[] buscarEmails(int[] ids) throws SQLException {
        StringBuilder sql = new StringBuilder();

        for (int id : ids) {
            if (sql.toString().equals("")) // troca o == para o .equals()
                sql = new StringBuilder(String.valueOf(id));
            else
                sql.append(", ").append(id);
        }

        if (!sql.toString().equals("")) { // troca o != para o .equals()
            List<String> emails = new ArrayList<>();

            try
                    (Connection conn = ConnectionDAO.getInstance().getConnection();
                     Statement stmt = conn.createStatement();

                     ResultSet rs = stmt.executeQuery("SELECT email FROM usuarios WHERE idUsuario IN (" + sql + ")")) {
                while (rs.next()) {
                    emails.add(rs.getString("email"));
                }

                return (String[]) emails.toArray();
            }
        } else
            return null;
    }

    public boolean podeCriarAta(int idUsuario) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery("SELECT COUNT(orgaos.idOrgao) AS qtde FROM orgaos " +
                         "WHERE idPresidente=" + idUsuario + " OR idSecretario=" + idUsuario)) {

            return rs.next() && rs.getInt("qtde") > 0;
        }
    }

}
