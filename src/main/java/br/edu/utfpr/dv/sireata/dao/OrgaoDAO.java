package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Orgao;
import br.edu.utfpr.dv.sireata.model.OrgaoMembro;
import br.edu.utfpr.dv.sireata.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrgaoDAO extends CommonMethods<Orgao> {
    public String buscarPorIdQuery() {
        return ("SELECT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
                "INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
                "INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
                "INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
                "WHERE orgaos.idOrgao = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return null;
    }

    public String excluirQuery(int id) {
        return null;
    }

    public String listarTodosQuery(boolean apenasAtivos) {
        return ("SELECT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
                "INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
                "INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
                "INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
                (apenasAtivos ? " WHERE orgaos.ativo=1" : "") + " ORDER BY orgaos.nome");
    }

    public String listarPorDepartamentoQuery(int idDepartamento) {
        return ("SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
                "INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
                "INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
                "INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
                "WHERE orgaos.idDepartamento = ? ORDER BY orgaos.nome");
    }

    public String listarParaCriacaoAtaQuery(int idDepartamento, int idUsuario) {
        return ("SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
                "INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
                "INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
                "INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
                "WHERE orgaos.ativo=1 AND orgaos.idDepartamento=" + idDepartamento + " AND (orgaos.idPresidente=" + idUsuario + " OR orgaos.idSecretario=" + idUsuario +
                ") ORDER BY orgaos.nome");
    }

    public String listarParaConsultaAtasQuery(int idDepartamento, int idUsuario) {
        return ("SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
                "INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
                "INNER JOIN atas ON atas.idOrgao=orgaos.idOrgao " +
                "INNER JOIN ataParticipantes ON ataParticipantes.idAta=atas.idAta " +
                "INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
                "INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
                "WHERE atas.publicada=0 AND ataParticipantes.presente=1 AND orgaos.idDepartamento=" + idDepartamento + " AND ataParticipantes.idUsuario=" + idUsuario +
                " ORDER BY orgaos.nome");
    }

    public List<Orgao> listarPorCampus(int idCampus) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
                                 "INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
                                 "INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
                                 "INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
                                 "WHERE departamentos.idCampus = ? ORDER BY departamentos.nome, orgaos.nome")) {

            stmt.setInt(1, idCampus);

            ResultSet rs = stmt.executeQuery();

            List<Orgao> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    public Usuario buscarPresidente(int idOrgao) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT idPresidente FROM orgaos WHERE idOrgao = ?")) {

            stmt.setInt(1, idOrgao);

            ResultSet rs = stmt.executeQuery();

            UsuarioDAO dao = new UsuarioDAO();
            return rs.next() ?
                    dao.buscarPorId(rs.getInt("idPresidente")) :
                    null;
        }
    }

    public Usuario buscarSecretario(int idOrgao) throws SQLException {

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT idSecretario FROM orgaos WHERE idOrgao = ?")) {

            stmt.setInt(1, idOrgao);

            ResultSet rs = stmt.executeQuery();

            UsuarioDAO dao = new UsuarioDAO();
            return rs.next() ?
                    dao.buscarPorId(rs.getInt("idSecretario")) :
                    null;
        }
    }

    public boolean isMembro(int idOrgao, int idUsuario) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT * FROM membros WHERE idOrgao = ? AND idUsuario=?")) {

            stmt.setInt(1, idOrgao);
            stmt.setInt(2, idUsuario);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    @Override
    public int inserir(Orgao orgao) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO orgaos(idDepartamento, idPresidente, idSecretario, nome, nomeCompleto, designacaoPresidente, ativo) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            conn.setAutoCommit(false);
            stmt.setInt(1, orgao.getDepartamento().getIdDepartamento());
            stmt.setInt(2, orgao.getPresidente().getIdUsuario());
            stmt.setInt(3, orgao.getSecretario().getIdUsuario());
            stmt.setString(4, orgao.getNome());
            stmt.setString(5, orgao.getNomeCompleto());
            stmt.setString(6, orgao.getDesignacaoPresidente());
            stmt.setInt(7, (orgao.isAtivo() ? 1 : 0));

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) orgao.setIdOrgao(rs.getInt(1));
            }

            stmt = conn.prepareStatement("DELETE FROM membros WHERE idOrgao=" + orgao.getIdOrgao());
            stmt.execute();

            for (OrgaoMembro u : orgao.getMembros()) {
                stmt = conn.prepareStatement("INSERT INTO membros(idOrgao, idUsuario, designacao) VALUES(?, ?, ?)");

                stmt.setInt(1, orgao.getIdOrgao());
                stmt.setInt(2, u.getUsuario().getIdUsuario());
                stmt.setString(3, u.getDesignacao());

                stmt.execute();
            }

            conn.commit();

            return orgao.getIdOrgao();
        }
    }

    @Override
    public int atualizar(Orgao orgao) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE orgaos SET idDepartamento=?, idPresidente=?, idSecretario=?, nome=?, nomeCompleto=?, designacaoPresidente=?, ativo=? WHERE idOrgao=?")
        ) {
            conn.setAutoCommit(false);

            stmt.setInt(1, orgao.getDepartamento().getIdDepartamento());
            stmt.setInt(2, orgao.getPresidente().getIdUsuario());
            stmt.setInt(3, orgao.getSecretario().getIdUsuario());
            stmt.setString(4, orgao.getNome());
            stmt.setString(5, orgao.getNomeCompleto());
            stmt.setString(6, orgao.getDesignacaoPresidente());
            stmt.setInt(7, (orgao.isAtivo() ? 1 : 0));

            stmt.setInt(8, orgao.getIdOrgao());

            stmt = conn.prepareStatement("DELETE FROM membros WHERE idOrgao=" + orgao.getIdOrgao());
            stmt.execute();

            for (OrgaoMembro u : orgao.getMembros()) {
                stmt = conn.prepareStatement("INSERT INTO membros(idOrgao, idUsuario, designacao) VALUES(?, ?, ?)");

                stmt.setInt(1, orgao.getIdOrgao());
                stmt.setInt(2, u.getUsuario().getIdUsuario());
                stmt.setString(3, u.getDesignacao());

                stmt.execute();
            }

            conn.commit();

            return orgao.getIdOrgao();
        } catch (SQLException e) {
            Connection conn = null;
            assert false;
            conn.rollback();

            throw e;
        }
    }

    @Override
    public int salvar(Orgao orgao) throws SQLException {
        boolean inserir = (orgao.getIdOrgao() == 0);

        return inserir ? inserir(orgao) : atualizar(orgao);
    }

    @Override
    Orgao carregarObjeto(ResultSet rs) throws SQLException {
        Orgao orgao = new Orgao();

        orgao.setIdOrgao(rs.getInt("idOrgao"));
        orgao.getDepartamento().setIdDepartamento(rs.getInt("idDepartamento"));
        orgao.getDepartamento().setNome(rs.getString("departamento"));
        orgao.getPresidente().setIdUsuario(rs.getInt("idPresidente"));
        orgao.getPresidente().setNome(rs.getString("presidente"));
        orgao.getSecretario().setIdUsuario(rs.getInt("idSecretario"));
        orgao.getSecretario().setNome(rs.getString("secretario"));
        orgao.setNome(rs.getString("nome"));
        orgao.setNomeCompleto(rs.getString("nomeCompleto"));
        orgao.setDesignacaoPresidente(rs.getString("designacaoPresidente"));
        orgao.setAtivo(rs.getInt("ativo") == 1);

        Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();

        ResultSet rs2 = stmt.executeQuery("SELECT membros.*, usuarios.nome FROM membros " +
                "INNER JOIN usuarios ON usuarios.idUsuario=membros.idUsuario " +
                "WHERE idOrgao=" + orgao.getIdOrgao() + " ORDER BY usuarios.nome");
        while (rs2.next()) {
            OrgaoMembro membro = new OrgaoMembro();

            membro.getUsuario().setIdUsuario(rs2.getInt("idUsuario"));
            membro.getUsuario().setNome(rs2.getString("nome"));
            membro.setDesignacao(rs2.getString("designacao"));

            orgao.getMembros().add(membro);
        }

        return orgao;
    }

}
