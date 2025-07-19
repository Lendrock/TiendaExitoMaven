drop database if exists tiendaexito;
create database tiendaexito;
use tiendaexito;

-- tabla productos
create table users(
	idUser int auto_increment,
    usuario varchar(50) not null,
    correo varchar(50) not null,
    contraseña varchar(255) not null,
    role enum ('admin', 'user') not null default 'user',
	constraint pk_users primary key (idUser)
);
create table Productos (
    idProducto int auto_increment primary key,
    nombre varchar(100) not null,
    descripcion text,
    precio decimal(10,2) not null,
    stock int not null,
    categoria varchar(50)
);

-- tabla compras
create table Compras (
    idCompra int auto_increment primary key,
    fecha datetime not null,
    idUser int not null,
    total decimal(10,2) not null,
    metodoPago varchar(50),
    estado varchar(30),
    constraint fk_compras_users
		foreign key (idUser) references users(idUser)
);

-- tabla detalleCompra con constraints explícitos
create table detalleCompra (
    idDetalle int auto_increment primary key,
    idCompra int not null,
    idProducto int not null,
    cantidad int not null,
    precioUnitario decimal(10,2) not null,
    subtotal decimal(10,2) not null,
    constraint fk_detalleCompra_compra
		foreign key (idCompra) references compras(idCompra),
    constraint fk_detalleCompra_producto 
		foreign key (idProducto) references productos(idProducto)
);

-- tabla de usuarios

-- __________________________Procedimientos almacenados____________________________________________

-- __________________________Procedimiento de Productos_____________________________________________

delimiter //
	create procedure sp_agregarProducto(
		in p_nombre varchar(100),
		in p_descripcion text,
		in p_precio decimal(10,2),
		in p_stock int,
		in p_categoria varchar(50)
	)
	begin
		insert into productos(nombre, descripcion, precio, stock, categoria)
		values (p_nombre, p_descripcion, p_precio, p_stock, p_categoria);
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_listarProductos()
	begin
		select * from productos;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_buscarProducto(in p_idProducto int)
	begin
		select * from productos where idProducto = p_idProducto;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_actualizarProducto(
		in p_idProducto int,
		in p_nombre varchar(100),
		in p_descripcion text,
		in p_precio decimal(10,2),
		in p_stock int,
		in p_categoria varchar(50)
	)
	begin
		update productos
		set nombre = p_nombre,
			descripcion = p_descripcion,
			precio = p_precio,
			stock = p_stock,
			categoria = p_categoria
		where idProducto = p_idProducto;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_eliminarProducto(in p_idProducto int)
	begin
		delete from productos where idProducto = p_idProducto;
	end //
delimiter ;

-- __________________________Procedimiento de Compras_______________________________________________

delimiter //
	create procedure sp_agregarCompra(
		in p_fecha datetime,
		in p_idUser int,
		in p_total decimal(10,2),
		in p_metodoPago varchar(50),
		in p_estado varchar(30)
	)
	begin
		insert into compras(fecha, idUser, total, metodoPago, estado)
		values (p_fecha, p_idUser, p_total, p_metodoPago, p_estado);
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_listarCompras()
	begin
		select * from compras;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_buscarCompra(in p_idCompra int)
	begin
		select * from compras where idCompra = p_idCompra;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_actualizarCompra(
		in p_idCompra int,
		in p_fecha datetime,
		in p_idUser int,
		in p_total decimal(10,2),
		in p_metodoPago varchar(50),
		in p_estado varchar(30)
	)
	begin
		update compras
		set fecha = p_fecha,
			idUser = p_idUser,
			total = p_total,
			metodoPago = p_metodoPago,
			estado = p_estado
		where idCompra = p_idCompra;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_eliminarCompra(in p_idCompra int)
	begin
		delete from compras where idCompra = p_idCompra;
	end //
delimiter ;

-- __________________________Procedimiento de detalleCompra	________________________________________

delimiter //
	create procedure sp_agregarDetalleCompra(
		in p_idCompra int,
		in p_idProducto int,
		in p_cantidad int,
		in p_precioUnitario decimal(10,2),
		in p_subtotal decimal(10,2)
	)
	begin
		insert into detalleCompra(idCompra, idProducto, cantidad, precioUnitario, subtotal)
		values (p_idCompra, p_idProducto, p_cantidad, p_precioUnitario, p_subtotal);
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_listarDetalleCompras()
	begin
		select * from detalleCompra;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_buscarDetalleCompra(in p_idDetalle int)
	begin
		select * from detalleCompra where idDetalle = p_idDetalle;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_actualizarDetalleCompra(
		in p_idDetalle int,
		in p_idCompra int,
		in p_idProducto int,
		in p_cantidad int,
		in p_precioUnitario decimal(10,2),
		in p_subtotal decimal(10,2)
	)
	begin
		update detalleCompra
		set idCompra = p_idCompra,
			idProducto = p_idProducto,
			cantidad = p_cantidad,
			precioUnitario = p_precioUnitario,
			subtotal = p_subtotal
		where idDetalle = p_idDetalle;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_eliminarDetalleCompra(in p_idDetalle int)
	begin
		delete from detalleCompra where idDetalle = p_idDetalle;
	end //
delimiter ;

-- Proceso almacenados para user
delimiter //
	create procedure sp_agregarUsuario(
		in p_usuario varchar(50),
		in p_correo varchar(50),
		in p_contrasena varchar(255),
		in p_role enum ('admin', 'user')
	)
	begin
		insert into users(usuario, correo, contraseña, role)
		values (p_usuario, p_correo, p_contrasena, p_role);
	end //
delimiter ;

--/////////////////////////////////////////////////////////////////////////////////////////////////

delimiter //
	create procedure sp_listarUsuarios()
	begin
		select idUser, usuario, correo, role from users;
	end //
delimiter ;

--/////////////////////////////////////////////////////////////////////////////////////////////////

delimiter //
	create procedure sp_buscarUsuario(in p_idUser int)
	begin
		select idUser, usuario, correo, role from users where idUser = p_idUser;
	end //
delimiter ;

--/////////////////////////////////////////////////////////////////////////////////////////////////

delimiter //
	create procedure sp_actualizarUsuario(
		in p_idUser int,
		in p_usuario varchar(50),
		in p_correo varchar(50),
		in p_contrasena varchar(255),
		in p_role enum ('admin', 'user')
	)
	begin
		update users
		set usuario = p_usuario,
			correo = p_correo,
			contraseña = p_contrasena,
			role = p_role
		where idUser = p_idUser;
	end //
delimiter ;

--/////////////////////////////////////////////////////////////////////////////////////////////////

delimiter //
	create procedure sp_eliminarUsuario(in p_idUser int)
	begin
		delete from users where idUser = p_idUser;
	end //
delimiter ;

delimiter //
	create procedure sp_verificarCredenciales(
		in p_usuario varchar(50),
        in p_contraseña varchar(255)
    )
    begin
		select role from users
        where usuario = p_usuario and contraseña = p_contraseña;
    end //
delimiter ;

delimiter //
	create procedure sp_listarProductosPorCategoria(
    in p_categoria varchar(50)
    )
	begin
		select idProducto, nombre, descripcion, precio, stock, categoria
        from Productos
        where categoria = p_categoria;
	end //
delimiter ;

call sp_agregarUsuario('admin','admin@admin.com','admin123','admin');
call sp_agregarUsuario('user','user@gmail.com','user123','user');

call sp_agregarProducto('Fender Stratocaster SSS', 'Guitarra eléctrica icónica.', 899.99, 15, 'Guitarras');
call sp_agregarProducto('Gibson Les Paul Standard', 'Guitarra eléctrica de caoba con humbuckers.', 2200.00, 8, 'Guitarras');
call sp_agregarProducto('Taylor 214ce', 'Guitarra acústica Grand Auditorium.', 1299.00, 10, 'Guitarras');
call sp_agregarProducto('Martin D-28', 'Clásica guitarra acústica Dreadnought.', 2799.00, 5, 'Guitarras');
call sp_agregarProducto('Ibanez RG550', 'Guitarra eléctrica super-strat de alto rendimiento.', 950.00, 12, 'Guitarras');
call sp_agregarProducto('Yamaha FG800', 'Guitarra acústica de iniciación.', 199.99, 25, 'Guitarras');

call sp_agregarProducto('Tama Imperialstar Kit', 'Set de batería acústica completo.', 799.00, 7, 'Baterias');
call sp_agregarProducto('Roland TD-1DMK', 'Batería electrónica compacta con pads de malla.', 699.00, 10, 'Baterias');
call sp_agregarProducto('Pearl Export Exx', 'Batería acústica versátil para intermedios.', 620.00, 9, 'Baterias');
call sp_agregarProducto('Alesis Nitro Mesh Kit', 'Kit de batería electrónica asequible con pads de malla.', 379.00, 18, 'Baterias');
call sp_agregarProducto('Ludwig Breakbeats', 'Kit de batería compacto y portátil.', 499.00, 6, 'Baterias');

call sp_agregarProducto('Yamaha P-45', 'Piano digital de 88 teclas contrapesadas.', 549.99, 15, 'Teclados');
call sp_agregarProducto('Korg Minilogue XD', 'Sintetizador analógico polifónico.', 849.00, 8, 'Teclados');
call sp_agregarProducto('Casio CDP-S150', 'Piano digital delgado y portátil.', 449.00, 20, 'Teclados');
call sp_agregarProducto('Native Instruments Komplete Kontrol S49', 'Teclado controlador MIDI avanzado.', 599.00, 10, 'Teclados');
call sp_agregarProducto('Arturia MicroFreak', 'Sintetizador híbrido experimental.', 349.00, 12, 'Teclados');

call sp_agregarProducto('Fender Jazz Bass Player Series', 'Bajo eléctrico de 4 cuerdas.', 799.00, 11, 'Bajos');
call sp_agregarProducto('Music Man StingRay Classic', 'Bajo eléctrico de 4 cuerdas potente.', 2000.00, 4, 'Bajos');
call sp_agregarProducto('Ibanez SR300E', 'Bajo eléctrico de 4 cuerdas rápido.', 399.00, 14, 'Bajos');
call sp_agregarProducto('Epiphone Thunderbird Vintage Pro', 'Bajo eléctrico clásico con gran sustain.', 549.00, 9, 'Bajos');
call sp_agregarProducto('Squier Precision Bass Affinity', 'Bajo eléctrico ideal para principiantes.', 249.00, 20, 'Bajos');

call sp_agregarCompra('2025-07-16 10:00:00', 2, 899.99, 'Tarjeta de Crédito', 'Completada');
call sp_agregarCompra('2025-07-16 11:30:00', 1, 699.00, 'PayPal', 'Pendiente');
call sp_agregarCompra('2025-07-17 09:00:00', 2, 549.99, 'Transferencia Bancaria', 'Completada');
call sp_agregarCompra('2025-07-17 14:15:00', 1, 799.00, 'Tarjeta de Débito', 'En Proceso');
call sp_agregarCompra('2025-07-18 08:00:00', 2, 349.00, 'Criptomoneda', 'Completada');
call sp_agregarCompra('2025-07-18 09:30:00', 1, 199.99, 'Tarjeta de Crédito', 'Completada');

call sp_agregarDetalleCompra(1, 1, 1, 899.99, 899.99);
call sp_agregarDetalleCompra(2, 8, 1, 699.00, 699.00);
call sp_agregarDetalleCompra(3, 12, 1, 549.99, 549.99);
call sp_agregarDetalleCompra(4, 17, 1, 799.00, 799.00);
call sp_agregarDetalleCompra(5, 16, 1, 349.00, 349.00);
call sp_agregarDetalleCompra(6, 6, 1, 199.99, 199.99);
call sp_agregarDetalleCompra(1, 10, 1, 379.00, 379.00);

select * from Users;

call sp_listarUsuarios;
