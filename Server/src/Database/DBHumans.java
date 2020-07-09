package Database;

import Collection.HumanBeingCollection;
import Human.HumanBeing;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DBHumans {
    Statement statement;
    Connection connection;

    public DBHumans(Connection connection) {
        try {
            this.connection = connection;
            this.statement = connection.createStatement();
            HumanBeingCollection humans = new HumanBeingCollection();
            loadCollection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadCollection() throws SQLException { //загрузить коллекцию из базы
        String sql = " SELECT * FROM humans";
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            HumanBeing human = new HumanBeing();
            human.setName(rs.getString("name"));
            human.setId(rs.getLong("id"));
            human.setOwner(rs.getString("owner"));
            HumanBeing.Coordinates coordinates = human.new Coordinates();
            coordinates.setX(rs.getDouble("x"));
            coordinates.setY(rs.getLong("y"));
            human.setCoordinates(coordinates);

            Timestamp timestamp = rs.getTimestamp("creationDate");
            LocalDateTime localDate = timestamp.toLocalDateTime();
            ZonedDateTime date = localDate.atZone(ZoneId.systemDefault());

            human.setCreationDate(date);
            human.setRealHero(rs.getBoolean("realHero"));
            human.setHasToothpick(rs.getBoolean("hasToothpick"));
            human.setImpactSpeed(rs.getLong("impactSpeed"));
            human.setMinutesOfWaiting(rs.getDouble("minutesOfWaiting"));
            human.setWeaponType(HumanBeing.WeaponType.valueOf(rs.getString("weaponType")));
            human.setMood(HumanBeing.Mood.valueOf(rs.getString("mood")));
            HumanBeing.Car car = human.new Car();
            car.setCarName(rs.getString("carName"));
            car.setCarCool(rs.getBoolean("carCool"));
            human.setCar(car);
            HumanBeingCollection.addHuman(human);
        }
        System.out.println("Коллекция из базы данных загружена.");
    }

    public void uploadCollection() { //выгрузить коллекцию в базу
        try {
            statement.execute("truncate humans");
            for (HumanBeing human : HumanBeingCollection.getCollection()) {
                this.uploadHuman(human);
            }
            System.out.println("Коллекция в базу данных загружена.");
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке данных в базу.");
            e.printStackTrace();
        }
    }

    public void uploadHuman(HumanBeing human) throws SQLException {
        PreparedStatement preparedStatement;
        if (human.getCar() != null) {
            String sql1 = "insert into humans (id, owner, name, x, y, creationdate, realhero, hastoothpick, impactspeed, minutesofwaiting, weapontype, mood, carname, carcool) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setLong(1, human.getId());
            preparedStatement.setString(2, human.getOwner());
            preparedStatement.setString(3, human.getName());
            preparedStatement.setDouble(4, human.getCoordinates().getX());
            preparedStatement.setLong(5, human.getCoordinates().getY());
            Timestamp timestamp = Timestamp.valueOf(human.getCreationDate().toLocalDateTime());
            preparedStatement.setTimestamp(6, timestamp);
            preparedStatement.setBoolean(7, human.isRealHero());
            preparedStatement.setBoolean(8, human.isHasToothpick());
            preparedStatement.setLong(9, human.getImpactSpeed());
            preparedStatement.setDouble(10, human.getMinutesOfWaiting());
            preparedStatement.setString(11, human.getWeaponType().toString());
            preparedStatement.setString(12, human.getMood().toString());
            preparedStatement.setString(13, human.getCar().getCarName());
            preparedStatement.setBoolean(14, human.getCar().isCarCool());
            preparedStatement.execute();
        } else {
            String sql2 = "insert into humans (id, owner, name, x, y, creationdate, realhero, hastoothpick, impactspeed, minutesofwaiting, weapontype, mood) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setLong(1, human.getId());
            preparedStatement.setString(2, human.getOwner());
            preparedStatement.setString(3, human.getName());
            preparedStatement.setDouble(4, human.getCoordinates().getX());
            preparedStatement.setLong(5, human.getCoordinates().getY());
            Timestamp timestamp = Timestamp.valueOf(human.getCreationDate().toLocalDateTime());
            preparedStatement.setTimestamp(6, timestamp);
            preparedStatement.setBoolean(7, human.isRealHero());
            preparedStatement.setBoolean(8, human.isHasToothpick());
            preparedStatement.setLong(9, human.getImpactSpeed());
            preparedStatement.setDouble(10, human.getMinutesOfWaiting());
            preparedStatement.setString(11, human.getWeaponType().toString());
            preparedStatement.setString(12, human.getMood().toString());
            preparedStatement.execute();
        }
    }
}


