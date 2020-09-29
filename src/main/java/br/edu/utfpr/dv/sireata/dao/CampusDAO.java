package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Campus;

import java.sql.*;
import java.util.List;

public class CampusDAO extends CommonMethods<Campus> {
    public String buscarPorIdQuery() {
        return ("SELECT * FROM campus WHERE idCampus = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return null;
    }

    public String excluirQuery(int id) {
        return null;
    }

    public String listarTodosQuery(boolean apenasAtivos) {
        return ("SELECT * FROM campus " + (apenasAtivos ? " WHERE ativo=1" : "") + " ORDER BY nome");
    }

    public String listarPorDepartamentoQuery(int idDepartamento) {
        return null;
    }

    public String listarParaCriacaoAtaQuery(int id, int idUsuario) {
        return null;
    }

    public String listarParaConsultaAtasQuery(int id, int idUsuario) {
        return null;
    }

    public Campus buscarPorDepartamento(int idDepartamento) throws SQLException {

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT idCampus FROM departamentos WHERE idDepartamento=?")) {

            stmt.setInt(1, idDepartamento);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ?
                        this.buscarPorId(rs.getInt("idCampus"))
                        : null;

            }


        }
    }

    @Override
    public int inserir(Campus campus) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO campus(nome, endereco, logo, ativo, site) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, campus.getNome());
            stmt.setString(2, campus.getEndereco());
            if (campus.getLogo() == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBytes(3, campus.getLogo());
            }
            stmt.setInt(4, campus.isAtivo() ? 1 : 0);
            stmt.setString(5, campus.getSite());

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) campus.setIdCampus(rs.getInt(1));
            }
            return campus.getIdCampus();
        }
    }

    @Override
    public int atualizar(Campus campus) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE campus SET nome=?, endereco=?, logo=?, ativo=?, site=? WHERE idCampus=?")
        ) {
            stmt.setString(1, campus.getNome());
            stmt.setString(2, campus.getEndereco());
            if (campus.getLogo() == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBytes(3, campus.getLogo());
            }
            stmt.setInt(4, campus.isAtivo() ? 1 : 0);
            stmt.setString(5, campus.getSite());

            stmt.setInt(6, campus.getIdCampus());
            stmt.execute();

            return campus.getIdCampus();
        }
    }

    @Override
    public int salvar(Campus campus) throws SQLException {
        boolean inserir = (campus.getIdCampus() == 0);

        return inserir ? inserir(campus) : atualizar(campus);
    }

    @Override
    Campus carregarObjeto(ResultSet rs) throws SQLException {
        Campus campus = new Campus();

        campus.setIdCampus(rs.getInt("idCampus"));
        campus.setNome(rs.getString("nome"));
        campus.setEndereco(rs.getString("endereco"));
        campus.setLogo(rs.getBytes("logo"));
        campus.setAtivo(rs.getInt("ativo") == 1);
        campus.setSite(rs.getString("site"));

        return campus;
    }

    public List<Campus> listarParaCriacaoAta(int idUsuario) throws SQLException {
        return super.listar("SELECT DISTINCT campus.* FROM campus " +
                "INNER JOIN departamentos ON departamentos.idCampus=campus.idCampus " +
                "INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
                "WHERE campus.ativo=1 AND (orgaos.idPresidente=" + idUsuario + " OR orgaos.idSecretario=" + idUsuario +
                ") ORDER BY campus.nome");
    }

    public List<Campus> listarParaConsultaAtas(int idUsuario) throws SQLException {
        return super.listar("SELECT DISTINCT campus.* FROM campus " +
                "INNER JOIN departamentos ON departamentos.idCampus=campus.idCampus " +
                "INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
                "INNER JOIN atas ON atas.idOrgao=orgaos.idOrgao " +
                "INNER JOIN ataParticipantes ON ataParticipantes.idAta=atas.idAta " +
                "WHERE atas.publicada=0 AND ataParticipantes.presente=1 AND ataParticipantes.idUsuario=" + idUsuario +
                " ORDER BY campus.nome");
    }
}
