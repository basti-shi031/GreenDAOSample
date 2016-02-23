# GreenDAOSample
基于GreenDAO框架的Sample

# GreenDAO使用

## 1.在工程中配置greenDao Generator模块

![](http://7xpvut.com1.z0.glb.clouddn.com/gd1.png)

 在.src/main目录下新建一个与java同层即的[java-gen]目录存放greenDAO生成的Bean,DAO,DaoMaster,DaoMaster.

## 2.配置app中的 build.gradle,分别添加 sourceSets 与 dependencies.
```xml
sourceSets{
    main{
        java.srcDirs = ['src/main/java','src/main/java-gen']
    }
}

dependencies {
    compile 'de.greenrobot:greendao:2.1.0'
}
```

![](http://7xpvut.com1.z0.glb.clouddn.com/gd2.png)

## 3.新建Generator模块(java library)

## 4.配置egenerator工程的 build.gradle，添加 dependencies.
```xml
compile 'de.greenrobot:greendao-generator:1.3.1'
```

## 5.编写数据库生成类
```java
public class Generator {
    public static void main(String[] args) throws Exception {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "com.basti.greendao");
//      当然，如果你愿意，你也可以分别指定生成的 Bean 与 DAO 类所在的目录，只要如下所示：
//      Schema schema = new Schema(1, "me.itangqi.bean");
//      schema.setDefaultJavaPackageDao("me.itangqi.dao");

        // 模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections。
        // schema2.enableActiveEntitiesByDefault();
        // schema2.enableKeepSectionsByDefault();

        // 一旦你拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了。
        addNote(schema);

        // 最后我们将使用 DAOGenerator 类的 generateAll() 方法自动生成代码，此处你需要根据自己的情况更改输出目录（既之前创建的 java-gen)。
        // 其实，输出目录的路径可以在 build.gradle 中设置，有兴趣的朋友可以自行搜索，这里就不再详解。
        new DaoGenerator().generateAll(schema, "F:\\AndroidStudioProject\\GreenDaoTest\\app\\src\\main\\java-gen");
    }

    private static void addNote(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Note」（既类名）
        Entity note = schema.addEntity("Note");
        // 你也可以重新给表命名
        // note.setTableName("NODE");

        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }
}
```

## 6.运行生成类，生成Bean

![](http://7xpvut.com1.z0.glb.clouddn.com/gd3.png)

## 7. 增删改查
在Application类中
```java
// 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
// 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
// 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
// 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
db = helper.getWritableDatabase();
// 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
daoMaster = new DaoMaster(db);
daoSession = daoMaster.newSession();
```

在查询的Activity中
```java
 noteDao = app.getDaoSession().getNoteDao();
```

增
```java
Note note = new Note(null, text, comment);
noteDao.insert(note);
```

删
```java
//根据Id删
noteDao.deleteByKey(deleteId);
//删全部
noteDao.deleteAll();
```

改
```java
Note note = list.get(position);
note.setText(text);
note.setComment(comment);

noteDao.update(note);
```

查
```java
String textColumn = NoteDao.Properties.Id.columnName;
String orderBy = textColumn + " COLLATE LOCALIZED ASC";
//cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);
query = noteDao.queryBuilder()
        .orderAsc(NoteDao.Properties.Id)
        .build();
list.addAll(query.list());
```
若需要有条件的查询则
```java
query = noteDao.queryBuilder()
        .orderAsc(NoteDao.Properties.Id)
        .where(NoteDao.Properties.Text.like("%"+text+"%"))
        .build();
```
