drop database if exists tiendaexito;
create database tiendaexito;
use tiendaexito;

-- tabla productos
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
    idCliente int not null,
    total decimal(10,2) not null,
    metodoPago varchar(50),
    estado varchar(30)
);

-- tabla detalle_compra con constraints explícitos
create table detalleCcompra (
    idDetalle int auto_increment primary key,
    idCompra int not null,
    idProducto int not null,
    cantidad int not null,
    precio_unitario decimal(10,2) not null,
    subtotal decimal(10,2) not null,
    constraint fk_detalle_compra_compra
		foreign key (idCompra) references compras(idCompra),
    constraint fk_detalle_compra_producto 
		foreign key (idProducto) references productos(idProducto)
);

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
		in p_idCliente int,
		in p_total decimal(10,2),
		in p_metodoPago varchar(50),
		in p_estado varchar(30)
	)
	begin
		insert into compras(fecha, idCliente, total, metodoPago, estado)
		values (p_fecha, p_idCliente, p_total, p_metodoPago, p_estado);
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
		in p_idCliente int,
		in p_total decimal(10,2),
		in p_metodoPago varchar(50),
		in p_estado varchar(30)
	)
	begin
		update compras
		set fecha = p_fecha,
			idCliente = p_idCliente,
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

-- __________________________Procedimiento de Detalle_Compra________________________________________

delimiter //
	create procedure sp_agregarDetalleCompra(
		in p_idCompra int,
		in p_idProducto int,
		in p_cantidad int,
		in p_precioUnitario decimal(10,2),
		in p_subtotal decimal(10,2)
	)
	begin
		insert into detalle_compra(idCompra, idProducto, cantidad, precio_unitario, subtotal)
		values (p_idCompra, p_idProducto, p_cantidad, p_precioUnitario, p_subtotal);
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_listarDetalleCompra()
	begin
		select * from detalle_compra;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_buscarDetalleCompra(in p_idDetalle int)
	begin
		select * from detalle_compra where idDetalle = p_idDetalle;
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
		update detalle_compra
		set idCompra = p_idCompra,
			idProducto = p_idProducto,
			cantidad = p_cantidad,
			precio_unitario = p_precioUnitario,
			subtotal = p_subtotal
		where idDetalle = p_idDetalle;
	end //
delimiter ;
 --/////////////////////////////////////////////////////////////////////////////////////////////////
delimiter //
	create procedure sp_eliminarDetalleCompra(in p_idDetalle int)
	begin
		delete from detalle_compra where idDetalle = p_idDetalle;
	end //
delimiter ;
