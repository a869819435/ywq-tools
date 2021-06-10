package com.ywq.tools.sqlTools;

import com.ywq.tools.common.util.ConvertUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 实体类生成
 */
public class BeanCreator {
    //表名
    public static String tableName = "tb_goods_info_D_logistics";
    public static String[] tableNames = new String[]{
            "tb_goods_info_G_system"
            ,"tb_goods_info_G_sale"
            ,"tb_goods_info_G_quality"
            ,"tb_goods_info_G_purchase"
            ,"tb_goods_info_G_photo"
            ,"tb_goods_info_G_management"
            ,"tb_goods_info_G_logistics"
            ,"tb_goods_info_G_display"
            ,"tb_goods_info_G_config"
            ,"tb_goods_info_G_common"};

    public static String idb = "mdm_goods";
    public static String url = "jdbc:mysql://10.249.0.121:30360/";
    public static String dbUser = "lbxrd";
    public static String password = "lbxrd@db654321";

    static {
        url = url + idb + "?useSSL=false&useUnicode=true&characterEncoding=utf8";
    }

    private static String getName(String colName) {
        String[] str = colName.split("_");
        if (str.length < 2) {
            return colName.toLowerCase();
        }
        StringBuffer buffer = new StringBuffer(str[0]);
        for (int i = 1; i < str.length; i++) {
            buffer.append((str[i].charAt(0) + "").toUpperCase()
                    + str[i].substring(1).toLowerCase());
        }
        return buffer.toString();
    }

    static Log log = LogFactory.getLog(BeanCreator.class);

    /**
     * 生成映射实体类 - 1
     *
     * @param tableName
     */
    private void createBean(String tableName) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            StringBuffer gettingBuffer = new StringBuffer();
            StringBuffer settingBuffer = new StringBuffer();
			/*System.out.println("import java.io.Serializable;\n");
			System.out.println("import java.util.Date;\n");
			System.out.println("import javax.persistence.Column;");
			System.out.println("import javax.persistence.Entity;");
			System.out.println("import javax.persistence.GeneratedValue;");
			System.out.println("import javax.persistence.GenerationType;");
			System.out.println("import javax.persistence.Id;");
			System.out.println("import javax.persistence.Table;\n");*/
			
			
			/*System.out.println("@Entity");
			System.out.println("@Table(name=\"" + tableName	+ "\")");*/
            //System.out.println("// " + getTableComment(tableName, con));
			/*System.out.println("public class " + getClassName(tableName)
					+ " implements Serializable{");
			System.out.println("");*/
            //System.out.println("public static String TABLE_NAME = \""+tableName+"\";\n");
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // the "@" annotation
                if (colName.toUpperCase().equals("ID")) { // Id
                    gettingBuffer.append("@Id" + "\n");
                    gettingBuffer.append("@GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                } else if (colName.toUpperCase().equals("CREATE_TIME")
                        || colName.toUpperCase().equals("UPDATE_TIME")) { // create_time

                    gettingBuffer.append("@Column(name=\"" + rsmd.getColumnLabel(i) + "\")" + "\n");
                } else {
                    gettingBuffer.append("@Column(name=\"" + rsmd.getColumnLabel(i) + "\")" + "\n");
                }
                //System.out.println("type="+type+" ="+getName(colName));
                // properties
                String comment = getColComment(tableName, colName, con);
                //System.out.println("comment="+comment);
                fieldBuffer.append("//" + comment + "\n");
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                }

                // 拼接
                String str1 = "private " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n";
                fieldBuffer.append(str1);

                String str2 = "public " + fieldType + " get"
                        + getName(colName).substring(0, 1).toUpperCase()
                        + getName(colName).substring(1) + "(){return "
                        + getName(colName) + ";}" + "\n";
                gettingBuffer.append(str2);

                String str3 = "public void set"
                        + getName(colName).substring(0, 1).toUpperCase()
                        + getName(colName).substring(1)
                        + "(" + fieldType + " " + getName(colName) + "){"
                        + "this." + getName(colName) + " = "
                        + getName(colName) + ";}" + "\n";
                settingBuffer.append(str3);

            }

            System.out.println(fieldBuffer.toString());
            System.out.println(gettingBuffer.toString());
            System.out.println(settingBuffer.toString());
            //System.out.println("}\n");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 生成映射实体类 - 2
     *
     * @param tableName
     */
    private void createBean2(String tableName) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String className = getClassName(tableName,"Entity") ;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            int count = rsmd.getColumnCount();
            fieldBuffer.append("import com.fasterxml.jackson.annotation.JsonFormat;\n" +
                    "import lombok.Data;\n" +
                    "import org.hibernate.annotations.GenericGenerator;\n" +
                    "\n" +
                    "import javax.persistence.*;\n" +
                    "import java.math.BigDecimal;\n" +
                    "import java.util.Date;\n\n");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            fieldBuffer.append("/**\n" +
                    " * 实体类 - 集团商品 | XX属性\n" +
                    " * @author ywq\n" +
                    " * @date " + formatter.format(new Date()) + "\n" +
                    " */\n" +
                    "@Data\n" +
                    "@Entity\n" +
                    "@Table(name = \"" + tableName + "\")\n");
            fieldBuffer.append("public class " + className + "{\n");
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // 注释
                String comment = getColComment(tableName, colName, con);
                fieldBuffer.append("\t/** " + comment + " */\n");

                // 注解
                if (colName.toUpperCase().equals("ID")) { // Id
                    fieldBuffer.append("\t@Id" + "\n");
                    fieldBuffer.append("\t@GenericGenerator(name = \"idGenerator\", strategy = \"uuid\")" + "\n");
                    fieldBuffer.append("\t@GeneratedValue(generator = \"idGenerator\")" + "\n");
                } else {
                    fieldBuffer.append("\t@Column(name=\"" + rsmd.getColumnLabel(i) + "\")" + "\n");
                }

                // 数据类型
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldBuffer.append("\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")" + "\n");
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                } else if (type == Types.DECIMAL) { // BigDecimal
                    fieldType = "BigDecimal";
                }
                // 拼接
                String str1 = "\tprivate " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n\n";
                fieldBuffer.append(str1);
            }
            fieldBuffer.append("}");
            String fileName = className + ".java";
            String pathFileName = tableName.substring(tableName.lastIndexOf("_") + 1);
            String pathName = "D:\\code\\java_code\\mycode\\generateCode\\mdm_goods\\domain\\" + pathFileName;
            String str = fieldBuffer.toString();
            this.generatFiles(pathName,fileName,str);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 生成表单-视图实体类
     *
     * @param tableName
     */
    private void createBeanForView(String tableName) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String className = getClassName(tableName,"View") ;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            int count = rsmd.getColumnCount();
            fieldBuffer.append("import com.alibaba.fastjson.annotation.JSONField;\n" +
                    "import com.fasterxml.jackson.annotation.JsonFormat;\n" +
                    "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                    "import lombok.Data;\n" +
                    "\n" +
                    "import java.math.BigDecimal;\n" +
                    "import java.util.Date;\n\n");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            fieldBuffer.append("/**\n" +
                    " * 查看类 - 集团公司商品 | XX属性\n" +
                    " *\n" +
                    " * @author ywq\n" +
                    " * @date " + formatter.format(new Date()) + "\n" +
                    " */\n" +
                    "@Data\n");
            fieldBuffer.append("public class " + className + "{\n");
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // 注释
                String comment = getColComment(tableName, colName, con);
                fieldBuffer.append("\t/** " + comment + " */\n");

                // 注解
                if (colName.contains("_")) {
                    fieldBuffer.append("\t@JsonProperty(\"" + colName + "\")" + "\n");
                }

                // 数据类型
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldBuffer.append("\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")" + "\n");
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                } else if (type == Types.DECIMAL) { // BigDecimal
                    fieldType = "BigDecimal";
                }

                // 拼接
                String str1 = "\tprivate " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n\n";
                fieldBuffer.append(str1);
            }
            fieldBuffer.append("}");
            String fileName = className + ".java";
            String pathFileName = tableName.substring(tableName.lastIndexOf("_") + 1);
            String pathName = "D:\\code\\java_code\\mycode\\generateCode\\mdm_goods\\domain\\" + pathFileName;
            String str = fieldBuffer.toString();
            this.generatFiles(pathName,fileName,str);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL执行异常");
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 生成Api实体类
     *
     * @param tableName
     */
    private void createBeanForModelApi(String tableName, boolean showPage) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // 注释
                String comment = getColComment(tableName, colName, con);
                fieldBuffer.append("/** " + comment + " */\n");

                // model-api 注解
                fieldBuffer.append("@ApiModelProperty(value = \"" + comment + "\", name = \"" + colName + "\")" + "\n");

                // 注解
                if (colName.contains("_")) {
                    fieldBuffer.append("@JsonProperty(\"" + colName + "\")" + "\n");
                }

                // 数据类型
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldBuffer.append("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")" + "\n");
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                } else if (type == Types.DECIMAL) { // BigDecimal
                    fieldType = "BigDecimal";
                }

                // 拼接
                String str1 = "private " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n\n";
                fieldBuffer.append(str1);
            }

            // 页码
            if (showPage) {
                fieldBuffer.append("/** 当前页 */\n");
                fieldBuffer.append("@ApiModelProperty(value = \"页码\", name = \"page\", required = true)\n");
                fieldBuffer.append("private Integer page;\n\n");
                fieldBuffer.append("/** 页大小 */\n");
                fieldBuffer.append("@ApiModelProperty(value = \"页大小\", name = \"size\", required = true)\n");
                fieldBuffer.append("private Integer size;\n");
            }

            System.out.println(fieldBuffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 生成表单-数据映射表单提交类
     *
     * @param tableName
     */
    private void createBeanForDTO(String tableName) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // 注释
                String comment = getColComment(tableName, colName, con);
                fieldBuffer.append("/** " + comment + " */\n");

                // 注解
                if (colName.contains("_")) {
                    fieldBuffer.append("@JsonProperty(\"" + colName + "\")" + "\n");
                }

                // 数据类型
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldBuffer.append("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")" + "\n");
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                } else if (type == Types.DECIMAL) { // BigDecimal
                    fieldType = "BigDecimal";
                }

                // 映射库表的字段
                if (colName.contains("_")) {
                    if ("Date".equals(fieldType)) {
                        fieldBuffer.append("@JSONField(name = \"" + colName + "\", format = \"yyyy-MM-dd HH:mm:ss\")\n");
                    } else {
                        fieldBuffer.append("@JSONField(name = \"" + colName + "\")\n");
                    }
                }

                // 拼接
                String str1 = "private " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n\n";
                fieldBuffer.append(str1);
            }

            System.out.println(fieldBuffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 生成查询条件实体类
     *
     * @param tableName
     */
    private void createBeanForQuery(String tableName) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // 注释
                String comment = getColComment(tableName, colName, con);
                fieldBuffer.append("/** " + comment + " */\n");

                // 注解
                if (colName.contains("_")) {
                    fieldBuffer.append("@JsonProperty(\"" + colName + "\")" + "\n");
                }

                // 数据类型
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldBuffer.append("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")" + "\n");
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                }

                // 拼接
                String str1 = "private " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n\n";
                fieldBuffer.append(str1);
            }
            // 页码
            fieldBuffer.append("/** 当前页 */\n");
            fieldBuffer.append("private Integer page;\n\n");
            fieldBuffer.append("/** 页大小 */\n");
            fieldBuffer.append("private Integer size;\n");

            System.out.println(fieldBuffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 生成实体类 -Excel映射
     *
     * @param tableName
     */
    private void createBeanForExcelDTO(String tableName) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer fieldBuffer = new StringBuffer();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String fieldType = "String";
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);

                // 注释
                String comment = getColComment(tableName, colName, con);
                fieldBuffer.append("/** " + comment + " */\n");

                // Excel注解
                fieldBuffer.append("@ExcelProperty(value = \"" + comment + "\")\n");

                // 注解
                if (colName.contains("_")) {
                    fieldBuffer.append("@JsonProperty(\"" + colName + "\")" + "\n");
                }

                // 数据类型
                if (type == 12 || type == Types.CLOB) { // String
                    fieldType = "String";

                } else if (type == Types.INTEGER || type == Types.DOUBLE) {
                    if (precision > 0) { // Double
                        fieldType = "Double";

                    } else { // Integer
                        fieldType = "Integer";

                    }
                } else if (type == Types.DATE || type == 93) { // Date
                    fieldBuffer.append("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")" + "\n");
                    fieldType = "Date";

                } else if (type == Types.BLOB) { // byte[]
                    fieldType = "byte[]";

                } else if (type == Types.BIGINT) { // long
                    fieldType = "long";

                } else if (type == Types.DECIMAL) { // BigDecimal
                    fieldType = "BigDecimal";
                }

                // 映射库表的字段
                if (colName.contains("_")) {
                    if ("Date".equals(fieldType)) {
                        fieldBuffer.append("@JSONField(name = \"" + colName + "\", format = \"yyyy-MM-dd HH:mm:ss\")\n");
                    } else {
                        fieldBuffer.append("@JSONField(name = \"" + colName + "\")\n");
                    }
                }

                // 拼接
                String str1 = "private " + fieldType + " " + getName(colName)
                        + getDefaultValue(tableName, colName) + ";" + "\n\n";
                fieldBuffer.append(str1);
            }

            System.out.println(fieldBuffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
    }

    /**
     * 获取表中字段的所有注释
     */
    public static List<String> getColumnComments(String tableName, Connection conn) {
        List<String> columnTypes = new ArrayList();
        PreparedStatement pStemt = null;
        String tableSql = "SELECT * FROM " + tableName;
        List<String> columnComments = new ArrayList();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }

    private String getDefaultValue(String tableName, String colName) {
        String result = null;
		/*Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionManager.getInstance().getConnection();
			String sql = "select data_default from user_tab_cols c where c.table_name=? and c.column_name=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, tableName.toUpperCase());
			pstmt.setString(2, colName.toUpperCase());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
				if (result != null) {
					if (result.equals("sysdate")) {
						result = "new Date()";
					}
					if (result.startsWith("'")) {
						result = "\""
								+ result.substring(1, result.length() - 1)
								+ "\"";
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.getInstance().close(con, pstmt, rs);
		}*/
        if (result != null) {
            result = "=" + result;
        } else {
            result = "";
        }
        return result;
    }

    private String getClassName(String tableName,String suffix) {
        String className = ConvertUtil.lineToHump(tableName.replaceAll("tb_","")) + suffix;
        className = className.substring(0,1).toUpperCase() + className.substring(1);
        return className;
    }

    private String getTableComment(String tablename, Connection con) throws SQLException {
        PreparedStatement pst = con
                .prepareStatement("select comments from user_tab_comments t where t.table_name=?");
        pst.setString(1, tablename.toUpperCase());
        ResultSet rs = pst.executeQuery();
        rs.next();
        String result = rs.getString(1);
        rs.close();
        pst.close();
        return result;
    }

    private String getColComment(String tablename, String columnname, Connection con) throws SQLException {
        PreparedStatement pst = con
                .prepareStatement("SELECT COLUMN_COMMENT FROM Information_schema.columns WHERE table_Name=? AND column_name=?");
        pst.setString(1, tablename);
        pst.setString(2, columnname);
        ResultSet rs = pst.executeQuery();
        String result = "";
        if (rs.next()) {
//            result = rs.getString("Comment");
            result = rs.getString(1);
        }
        rs.close();
        pst.close();
        return result;
    }

    public void createInsert(String tableName) {
        System.out.println(ConstantString.getHeaderWithOutRS());
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer setvalue = new StringBuffer();
            StringBuffer cols = new StringBuffer();
            StringBuffer quest = new StringBuffer();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String colName = rsmd.getColumnName(i).toLowerCase();
                cols.append(colName + ",");
                quest.append("?,");
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);
                if (type == 12) {
                    setvalue.append("pstmt.setString(" + i + ", instance.get"
                            + colName.toUpperCase().charAt(0)
                            + colName.substring(1) + "());");
                } else if (type == 2) {
                    if (precision > 0) {
                        setvalue.append("pstmt.setDouble(" + i
                                + ", instance.get"
                                + colName.toUpperCase().charAt(0)
                                + colName.substring(1) + "());");
                    } else {
                        setvalue.append("pstmt.setLong(" + i + ", instance.get"
                                + colName.toUpperCase().charAt(0)
                                + colName.substring(1) + "());");
                    }
                } else {
                    setvalue.append("pstmt.setNoType(" + i + ", instance.get"
                            + colName.toUpperCase().charAt(0)
                            + colName.substring(1) + "());");
                }
                if (log.isDebugEnabled()) {
                    log.debug("colname:" + colName + "; type:" + type
                            + ";precision:" + precision);
                }
            }
            String sql = "insert into " + tableName + "("
                    + cols.substring(0, cols.length() - 1) + ") values("
                    + quest.substring(0, quest.length() - 1) + ")";
            System.out.println(sql);
            System.out.println(setvalue);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
        System.out.println(ConstantString.getFooterWithOutRS());
    }

    public void createGet(String tableName) {
        System.out.println(ConstantString.getHeaderWithRS());
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer getvaliue = new StringBuffer();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String colName = rsmd.getColumnName(i).toLowerCase();
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);
                if (type == 12) {
                    getvaliue.append("instance.set"
                            + colName.toUpperCase().charAt(0)
                            + colName.substring(1) + "(rs.getString(\""
                            + colName + "\"));");
                } else if (type == 2) {
                    if (precision > 0) {
                        getvaliue.append("instance.set"
                                + colName.toUpperCase().charAt(0)
                                + colName.substring(1) + "(rs.getDouble(\""
                                + colName + "\"));");
                    } else {
                        getvaliue.append("instance.set"
                                + colName.toUpperCase().charAt(0)
                                + colName.substring(1) + "(rs.getLong(\""
                                + colName + "\"));");
                    }
                } else {
                    getvaliue.append("instance.set"
                            + colName.toUpperCase().charAt(0)
                            + colName.substring(1) + "(pstmt.getNoType(\""
                            + colName + "\"));");
                }
                if (log.isDebugEnabled()) {
                    log.debug("colname:" + colName + "; type:" + type
                            + ";precision:" + precision);
                }
            }
            System.out.println(getvaliue);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
        System.out.println(ConstantString.getFooterWithRS());
    }

    public void createUpdate(String tableName) {
        System.out.println(ConstantString.getHeaderWithOutRS());
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer setvalue = new StringBuffer();
            StringBuffer sqlBf = new StringBuffer("update " + tableName
                    + " set ");
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String colName = rsmd.getColumnName(i).toLowerCase();
                sqlBf.append(colName + "=?,");
                int type = rsmd.getColumnType(i);
                int precision = rsmd.getScale(i);
                if (type == 12) {
                    setvalue.append("pstmt.setString(" + i + ", instance.get"
                            + colName.toUpperCase().charAt(0)
                            + colName.substring(1) + "());");
                } else if (type == 2) {
                    if (precision > 0) {
                        setvalue.append("pstmt.setDouble(" + i
                                + ", instance.get"
                                + colName.toUpperCase().charAt(0)
                                + colName.substring(1) + "());");
                    } else {
                        setvalue.append("pstmt.setLong(" + i + ", instance.get"
                                + colName.toUpperCase().charAt(0)
                                + colName.substring(1) + "());");
                    }
                } else {
                    setvalue.append("pstmt.setNoType(" + i + ", instance.get"
                            + colName.toUpperCase().charAt(0)
                            + colName.substring(1) + "());");
                }
                if (log.isDebugEnabled()) {
                    log.debug("colname:" + colName + "; type:" + type
                            + ";precision:" + precision);
                }
            }
            String sql = sqlBf.substring(0, sqlBf.length() - 1) + ") where ";
            System.out.println(sql);
            System.out.println(setvalue);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.getInstance().close(con, stmt, rs);
        }
        System.out.println(ConstantString.getFooterWithOutRS());
    }

    private void createDao(String tableName) {
        StringBuffer fileStr = new StringBuffer();
        String entity = getClassName(tableName,"Entity");
        String repository = getClassName(tableName,"Repository");
        String pathFileName = tableName.substring(tableName.lastIndexOf("_") + 1);
        fileStr.append("import com.lbx.savedata.slave.goods.domain.GoodsInfoQuery;\n" +
                "import com.lbx.savedata.slave.goods.domain.g." + pathFileName + "."+ entity + ";\n" +
                "import org.springframework.data.domain.Page;\n" +
                "import org.springframework.data.domain.Pageable;\n" +
                "import org.springframework.data.jpa.repository.JpaRepository;\n" +
                "import org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n" +
                "import org.springframework.data.jpa.repository.Query;\n" +
                "import org.springframework.stereotype.Repository;\n\n");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        fileStr.append("/**\n" +
                " * 数据访问接口 - xxx【正式库】\n" +
                " *\n" +
                " * @author ywq\n" +
                " * @date " + formatter.format(new Date()) + "\n" +
                " */\n");
        fileStr.append("@Repository\n");
        fileStr.append("public interface " + repository
                + " extends " + "JpaRepository<" + entity + ", String>,\n" +
                "       JpaSpecificationExecutor<" + entity + "> {\n");
        fileStr.append("/**\n" +
                "     * 分页条件查询\n" +
                "     *\n" +
                "     * @param queryBean 查询条件\n" +
                "     * @param pageable  分页\n" +
                "     * @return 查询结果\n" +
                "     */\n" +
                "    @Query(value = \"SELECT t.* \" +\n" +
                "            \" FROM " + tableName + " t \" +\n" +
                "            \" WHERE t.`status` <> 0 \" +\n" +
                "            \" AND (coalesce(:#{#queryBean.udfCodes}, NULL) IS NULL OR t.udf_code IN(:#{#queryBean.udfCodes})) \" +\n" +
                "            \" AND (coalesce(:#{#queryBean.bizDeptIds}, NULL) IS NULL OR t.biz_dept_id IN(:#{#queryBean.bizDeptIds})) \",\n" +
                "            countProjection = \"t.id\",\n" +
                "            nativeQuery = true)\n" +
                "    Page<" + entity + "> getListByQuery(GoodsInfoQuery queryBean, Pageable pageable);");
        fileStr.append("}");
        String fileName = repository + ".java";
        String pathName = "D:\\code\\java_code\\mycode\\generateCode\\mdm_goods\\repository\\";
        String str = fileStr.toString();
        this.generatFiles(pathName,fileName,str);
    }

    private void createService(String tableName) {
        System.out.println();
        System.out.println();

        System.out.println("@Service");
        System.out.println("@Transactional(readOnly = true)");
        System.out.println("public class " + getClassName(tableName,"Service{"));
        System.out.println("");
        System.out.println("");
        System.out.println("@Autowired");
        String className = getClassName(tableName,"");
        String a = className.substring(0, 1);
        className = a.toLowerCase() + className.substring(1, className.length());
        System.out.println("private " + getClassName(tableName,"Repository") + className + "Repository;");
        System.out.println("}");
    }

    private void generatFiles(String path,String fileName,String str){
        try {
            File file = new File(path);
            //如果文件夹不存在
            if(!file.exists()){
                //创建文件夹
                file.mkdirs();
            }
            file = new File(path, fileName);
            // 创建文件
            file.createNewFile();
            // 向文件写入内容(输出流)
            byte bt[] = str.getBytes();
            FileOutputStream in = new FileOutputStream(file);
            in.write(bt, 0, bt.length);
            in.close();
            System.out.println("写入文件成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("文件创建失败");
        }  catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件写入失败");
        }
    }

    public static void main(String[] args) {
        BeanCreator creator = new BeanCreator();
        System.out.println();
        for (String tableName : tableNames) {
//    		creator.createBean2(tableName);
//            creator.createBeanForView(tableName);
            creator.createDao(tableName);
            System.out.println();
        }
    }
}
