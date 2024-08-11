package com.tubiblioteca.controller.ABMMiembro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.App;
import com.tubiblioteca.config.CargadorDeVistas;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import java.net.URL;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.VistasFXML;
import com.tubiblioteca.helper.Alerta;
import javafx.event.EventHandler;

public class ListaMiembrosControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Miembro, String> colDni;
    @FXML
    private TableColumn<Miembro, String> colNombre;
    @FXML
    private TableColumn<Miembro, TipoMiembro> colTipo;

    // Tabla de miembros
    @FXML
    private TableView<Miembro> tblMiembros;

    // Filtros adicionales
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<TipoMiembro> cmbTipo;

    // Listas utilizadas
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<Miembro> filtrados = FXCollections.observableArrayList();
    private final ObservableList<TipoMiembro> tipos = FXCollections.observableArrayList();

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(ListaMiembrosControlador.class);

    private Miembro miembro;
    private MiembroServicio servicio;
    private FormularioMiembroControlador formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos la tabla de miembros
        inicializarTablaMiembros();
        // Inicializamos los filtros
        inicializarFiltros();
    }

    public void inicializarTablaMiembros() {
        try {
            servicio = new MiembroServicio(App.getRepositorio());

            // Asociamos columnas de la tabla
            colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

            // Establecemos una fábrica de celdas para la columna
            // Devolvemos una nueva celda para la visualizacion de datos
            colNombre.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String nombre, boolean empty) {
                    // Llamamos a la clase padre para cualquier inicializacion necesaria
                    super.updateItem(nombre, empty);
                    // Verificamos si el nombre es nulo
                    if (nombre == null || empty) {
                        setText(null);
                    } else {
                        // Si es diferente de nulo, concatenamos el nombre y apellido del miembro
                        // correspondiente
                        setText(nombre + " " + tblMiembros.getItems().get(getIndex()).getApellido());
                    }
                }
            });

            // Limpiamos las listas listas
            miembros.clear();
            filtrados.clear();

            // Obtenemos todos los miembros
            miembros.addAll(servicio.buscarTodos());

            // Cargamos el filtro de miembros
            filtrados.addAll(miembros);

            // Cargamos la tabla
            tblMiembros.setItems(filtrados);

        } catch (Exception e) {
            log.error("Error al inicializar la lista de miembros: ", e, e.getCause());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de cargos
        tipos.clear();
        tipos.addAll(TipoMiembro.values());
        cmbTipo.setItems(tipos);
    }

    @FXML
    private void agregar() {

        try {

            FXMLLoader loader = CargadorDeVistas.crearLoader(VistasFXML.FormularioMiembro.getRutaFxml());

            // Cargamos el formulario
            Parent root = loader.load();

            formulario = loader.getController();

            formulario.setServicio(servicio);

            // Abrimos modal
            App.abrirModal(root, VistasFXML.FormularioMiembro);

            System.out.println(formulario.getMiembro());

            miembro = formulario.getMiembro();

            if (miembro != null) {
                miembros.add(miembro);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(miembro)) {
                    filtrados.add(miembro);
                }

                tblMiembros.refresh();
            }
        } catch (Exception e) {
            log.error("Error: ", e, e.getCause());
        }
    }

    @FXML
    private void eliminar() {

        // Obtenemos miembro seleccionado
        Miembro miembro = tblMiembros.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (miembro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un miembro!");
        } else {

            if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el miembro?")) {
                try {
                    // Eliminamos el miembro
                    servicio.borrar(miembro);

                    // Actualizamos las listas
                    miembros.remove(miembro);
                    filtrados.remove(miembro);

                    // Mostramos un mensaje
                    Alerta.mostrarMensaje(false, "Info", "Se ha eliminado el miembro correctamente!");

                    // Cerramos sesion, en caso de que sea el que haya iniciado sesion
                    /*
                     * if (sessionManager.autenticarMiembro(miembro)) {
                     * sessionManager.cerrarSesion();
                     * }
                     */

                    // Actualizamos la tabla de miembros
                    tblMiembros.refresh();

                } catch (Exception e) {
                    log.error("", e, e.getCause());
                    Alerta.mostrarMensaje(true, "Info", "No se pudo eliminar el miembro correctamente!\n" +
                            "Es posible que esté vinculado a otros registros en el sistema.");
                }
            }
        }

    }

    @FXML
    private void modificar() {
        // Obtenemos miembro seleccionado
        miembro = tblMiembros.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (miembro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un miembro!");
        } else {
            try {
                FXMLLoader loader = CargadorDeVistas.crearLoader(VistasFXML.FormularioMiembro.getRutaFxml());

                // Cargamos el formulario
                Parent root = loader.load();

                formulario = loader.getController();

                formulario.setMiembro(miembro);
                formulario.setServicio(servicio);

                // Abrimos modal
                App.abrirModal(root, VistasFXML.FormularioMiembro);

                if (quitarFiltro(miembro)) {
                    filtrados.remove(miembro);
                }

                tblMiembros.refresh();
            } catch (Exception e) {
                log.error("", e, e.getCause());
            }

        }

    }

    // Metodos para filtrar los miembros

    @FXML
    private void filtrar() {
        // Limpiamos la lista filtro de miembros
        filtrados.clear();

        // Filtramos todos los miembros
        for (Miembro m : miembros) {
            if (aplicarFiltro(m)) {
                filtrados.add(m);
            }
        }

        // Actualizamos la tabla
        tblMiembros.refresh();
    }

    public boolean aplicarFiltro(Miembro m) {
        // Obtenemos todos los filtros
        String dni = txtDni.getText().trim().toLowerCase();
        String nombre = txtNombre.getText().trim().toLowerCase();
        TipoMiembro tipo = cmbTipo.getValue();

        // Filtramos con base en los filtros obtenidos
        return (dni == null || m.getDni().toLowerCase().contains(dni))
                && (nombre == null || m.toString().toLowerCase().contains(nombre))
                && (tipo == null || tipo.equals(m.getTipo()));
    }

    public boolean quitarFiltro(Miembro m) {
        // Obtenemos todos los filtros
        String dni = txtDni.getText().trim().toLowerCase();
        String nombre = txtNombre.getText().trim().toLowerCase();
        TipoMiembro tipo = cmbTipo.getValue();

        // Quitamos del filtro con base en los filtros obtenidos
        return (dni != null && !m.getDni().toLowerCase().contains(dni))
                || (nombre != null && !m.toString().toLowerCase().contains(nombre))
                || (tipo != null && !tipo.equals(m.getTipo()));
    }

    @FXML
    private void limpiarFiltros() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerTipo = cmbTipo.getOnAction();

        // Desactiva1mos los eventos asociados a los filtros
        cmbTipo.setOnAction(null);

        // Limpiamos los filtros
        txtDni.clear();
        txtNombre.clear();
        cmbTipo.setValue(null);

        // Filtramos los expedientes
        filtrar();

        // Restauramos los eventos asociados a los filtros
        cmbTipo.setOnAction(handlerTipo);
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }

}
