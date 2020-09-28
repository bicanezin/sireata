package  br.edu.utfpr.dv.sireata.dao;

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

    public M buscarPorId(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.prepareStatement(buscarPorIdQuery());

            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if(rs.next()){
                return this.carregarObjeto(rs);
            }else{
                return null;
            }
        }finally{
            if((rs != null) && !rs.isClosed())
                rs.close();
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }

    public List<M> listarPorAta(int idAta) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(listarPorAtaQuery(idAta));

            List<M> list = new ArrayList<>();

            while(rs.next()){
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }finally{
            if((rs != null) && !rs.isClosed())
                rs.close();
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }

    public abstract int salvar(M m) throws SQLException;

    public void excluir(int id) throws SQLException{
        Connection conn = null;
        Statement stmt = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.createStatement();

            stmt.execute(excluirQuery(id));
        }finally{
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }

    abstract M carregarObjeto(ResultSet rs) throws SQLException;

    public List<M> listarTodos(boolean apenasAtivos) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(listarTodosQuery(apenasAtivos));

            List<M> list = new ArrayList<>();

            while(rs.next()){
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }finally{
            if((rs != null) && !rs.isClosed())
                rs.close();
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }

    public List<M> listarPorDepartamento(int idDepartamento) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.prepareStatement(
                    listarPorDepartamentoQuery(idDepartamento));

            stmt.setInt(1, idDepartamento);

            rs = stmt.executeQuery();

            List<M> list = new ArrayList<>();

            while(rs.next()){
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }finally{
            if((rs != null) && !rs.isClosed())
                rs.close();
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }

    public List<M> listarParaCriacaoAta(int idCampus, int idUsuario) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(listarParaCriacaoAtaQuery(idCampus, idUsuario));

            List<M> list = new ArrayList<>();

            while(rs.next()){
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }finally{
            if((rs != null) && !rs.isClosed())
                rs.close();
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }

    public List<M> listarParaConsultaAtas(int idCampus, int idUsuario) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(listarParaConsultaAtasQuery(idCampus, idUsuario));

            List<M> list = new ArrayList<>();

            while(rs.next()){
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }finally{
            if((rs != null) && !rs.isClosed())
                rs.close();
            if((stmt != null) && !stmt.isClosed())
                stmt.close();
            if((conn != null) && !conn.isClosed())
                conn.close();
        }
    }
}