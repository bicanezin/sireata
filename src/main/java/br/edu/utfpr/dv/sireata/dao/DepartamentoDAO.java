package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Departamento;

import java.sql.*;
import java.util.List;

public class DepartamentoDAO extends CommonMethods<Departamento> {
    public String buscarPorIdQuery() {
        return ("SELECT departamentos.*, campus.nome AS nomeCampus " +
                "FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
                "WHERE idDepartamento = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return null;
    }

    public String excluirQuery(int id) {
        return null;
    }

    public String listarTodosQuery(boolean apenasAtivos) {
        return ("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus " +
                "FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
                (apenasAtivos ? " WHERE departamentos.ativo=1" : "") + " ORDER BY departamentos.nome");
    }

    public String listarPorDepartamentoQuery(int idDepartamento) {
        return null;
    }

    public String listarParaCriacaoAtaQuery(int idCampus, int idUsuario) {
        return ("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus FROM departamentos " +
                "INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
                "INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
                "WHERE departamentos.ativo=1 AND departamentos.idCampus=" + idCampus + " AND (orgaos.idPresidente=" + idUsuario + " OR orgaos.idSecretario=" + idUsuario +
                ") ORDER BY departamentos.nome");
    }

    public String listarParaConsultaAtasQuery(int idCampus, int idUsuario) {
        return ("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus FROM departamentos " +
                "INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
                "INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
                "INNER JOIN atas ON atas.idOrgao=orgaos.idOrgao " +
                "INNER JOIN ataParticipantes ON ataParticipantes.idAta=atas.idAta " +
                "WHERE atas.publicada=0 AND ataParticipantes.presente=1 AND departamentos.idCampus=" + idCampus + " AND ataParticipantes.idUsuario=" + idUsuario +
                " ORDER BY departamentos.nome");
    }

    public Departamento buscarPorOrgao(int idOrgao) throws SQLException {
        return super.buscar(idOrgao, "SELECT departamentos.*, campus.nome AS nomeCampus " +
                "FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
                "INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
                "WHERE orgaos.idOrgao = ?");
    }

    public List<Departamento> listarPorCampus(int idCampus, boolean apenasAtivos) throws SQLException {
        return super.listar("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus " +
                "FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
                "WHERE departamentos.idCampus=" + idCampus + (apenasAtivos ? " AND departamentos.ativo=1" : "") + " ORDER BY departamentos.nome");
    }

    @Override
    public int inserir(Departamento departamento) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO departamentos(idCampus, nome, logo, ativo, site, nomeCompleto) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, departamento.getCampus().getIdCampus());
            stmt.setString(2, departamento.getNome());
            if (departamento.getLogo() == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBytes(3, departamento.getLogo());
            }
            stmt.setInt(4, departamento.isAtivo() ? 1 : 0);
            stmt.setString(5, departamento.getSite());
            stmt.setString(6, departamento.getNomeCompleto());

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) departamento.setIdDepartamento(rs.getInt(1));
            }
            return departamento.getIdDepartamento();
        }
    }

    @Override
    public int atualizar(Departamento departamento) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE departamentos SET idCampus=?, nome=?, logo=?, ativo=?, site=?, nomeCompleto=? WHERE idDepartamento=?")
        ) {
            stmt.setInt(1, departamento.getCampus().getIdCampus());
            stmt.setString(2, departamento.getNome());
            if (departamento.getLogo() == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBytes(3, departamento.getLogo());
            }
            stmt.setInt(4, departamento.isAtivo() ? 1 : 0);
            stmt.setString(5, departamento.getSite());
            stmt.setString(6, departamento.getNomeCompleto());

            stmt.setInt(7, departamento.getIdDepartamento());

            stmt.execute();

            return departamento.getIdDepartamento();
        }
    }

    @Override
    public int salvar(Departamento departamento) throws SQLException {
        boolean inserir = (departamento.getIdDepartamento() == 0);

        return inserir ? inserir(departamento) : atualizar(departamento);
    }

    @Override
    Departamento carregarObjeto(ResultSet rs) throws SQLException {
        Departamento departamento = new Departamento();

        departamento.setIdDepartamento(rs.getInt("idDepartamento"));
        departamento.getCampus().setIdCampus(rs.getInt("idCampus"));
        departamento.setNome(rs.getString("nome"));
        departamento.setNomeCompleto(rs.getString("nomeCompleto"));
        departamento.setLogo(rs.getBytes("logo"));
        departamento.setAtivo(rs.getInt("ativo") == 1);
        departamento.setSite(rs.getString("site"));
        departamento.getCampus().setNome(rs.getString("nomeCampus"));

        return departamento;
    }

}
