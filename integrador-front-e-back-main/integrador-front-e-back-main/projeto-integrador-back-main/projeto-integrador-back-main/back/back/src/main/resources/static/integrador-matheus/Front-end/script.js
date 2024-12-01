const modal = document.querySelector(".modal-container");
const tbody = document.querySelector("tbody");
const sModelo = document.querySelector("#m-modelo");
const sAno = document.querySelector("#m-ano");
const sPreco = document.querySelector("#m-preco");
const sCor = document.querySelector("#m-cor");
const sPlaca = document.querySelector("#m-placa");
const btnSalvar = document.querySelector("#btnSalvar");
const modalConfirm = document.querySelector("#modalConfirm");
const btnConfirmDelete = document.querySelector("#btnConfirmDelete");
const btnCancelDelete = document.querySelector("#btnCancelDelete");

let itens = [];
let modelo;
let id = null; 

function limitarAno(event) {
  const campo = event.target;
  let valor = campo.value.replace(/\D/g, '');  
   
  if (valor.length > 4) {
    valor = valor.slice(0, 4);
  }
 
  if (parseInt(valor) > 2024) {
    valor = '2024';
  }
  
  campo.value = valor;
}

  
// Função para deixar a placa igual na vida real
function formatarPlaca(event) {
  const campo = event.target;
  let valor = campo.value.toUpperCase();

  valor = valor.replace(/[^A-Za-z0-9]/g, '');

  let parte1 = valor.slice(0, 3);
  let parte2 = valor.slice(3, 7);

  parte1 = parte1.replace(/[^A-Za-z]/g, '');
  parte2 = parte2.replace(/[^0-9]/g, '');

  if (parte2.length > 4) {
    parte2 = parte2.slice(0, 4);
  }

  valor = parte1 + (parte2 ? '-' + parte2 : '');

  campo.value = valor;
}

// Função para colocar a vírgula no preço
function virgula(event) {
  const campo = event.target;
  let valor = campo.value.replace(/\D/g, '');   
 
  if (valor.length > 11) {
    valor = valor.slice(0, 11);
  }
 
  valor = valor.replace(/\B(?=(\d{3})+(?!\d))/g, ',');

  campo.value = valor;
}



function openModal(edit = false, index = 0) {
  modal.classList.add("active");

  modal.onclick = (e) => {
    if (e.target.className.indexOf("modal-container") !== -1) {
      modal.classList.remove("active");
    }
  };

  if (edit) {
    sModelo.value = itens[index].modelo;
    sAno.value = itens[index].ano;
    sPreco.value = itens[index].preco;
    sCor.value = itens[index].cor;
    sPlaca.value = itens[index].placa;
    id = itens[index].id; 
  } else {
    sModelo.value = "";
    sAno.value = "";
    sPreco.value = "";
    sCor.value = "";
    sPlaca.value = "";
    id = null;  
  }
}

function editItem(index) {
  openModal(true, index);
}

function showConfirmDeleteModal(index) {
  itemToDelete = index;
  modalConfirm.classList.add("active");
}

btnConfirmDelete.onclick = () => {
  if (itemToDelete !== null) {
    deleteItem(itemToDelete);
    itemToDelete = null;
  }
  modalConfirm.classList.remove("active");
};

btnCancelDelete.onclick = () => {
  itemToDelete = null;
  modalConfirm.classList.remove("active");
};

btnSalvar.onclick = (e) => {
  e.preventDefault();
  if (sModelo.value === "" || sAno.value === "" || sPreco.value === "" || sCor.value === "" || sPlaca.value === "") {
    return;
  }

  let tr = document.createElement("tr");
  tr.innerHTML = `
    <td><img src="${item.imagem}" alt="Imagem do Carro" style="max-width: 100px; max-height: 100px;" /></td>
    <td>${item.modelo}</td>
    <td>${item.ano}</td>
    <td>${item.preco}</td>
    <td>${item.cor}</td>
    <td>${item.placa}</td>
    <td class="acao">
      <button onclick="editItem(${index})"><i class='bx bx-edit'></i></button>
    </td>
    <td class="acao">
      <button onclick="showConfirmDeleteModal(${index})"><i class='bx bx-trash'></i></button>
    </td>
  `;
  tbody.appendChild(tr);
  
}

//CONECTANDO O FRONT COM O BACK
fetch('http://localhost:8080/carro/listar', {
  method: 'GET',
})
  .then(response => response.json())
  .then(data => {
      itens = data;   
      renderTable();   
  })
  .catch(error => {
      console.error('Erro ao carregar os dados:', error);
  });

function renderTable() {
  tbody.innerHTML = '';   
  itens.forEach((item, index) => {
      let tr = document.createElement("tr");
      tr.innerHTML = `
          <td><img src="${item.imagem}" alt="Imagem do Carro" style="max-width: 100px; max-height: 100px;" /></td>
          <td>${item.modelo}</td>
          <td>${item.ano}</td>
          <td>${item.preco}</td>
          <td>${item.cor}</td>
          <td>${item.placa}</td>
          <td class="acao">
              <button onclick="editItem(${index})"><i class='bx bx-edit'></i></button>
          </td>
          <td class="acao">
              <button onclick="showConfirmDeleteModal(${index})"><i class='bx bx-trash'></i></button>
          </td>
      `;
      tbody.appendChild(tr);
  });
}

// Adicionando os eventos de formatação nos campos de entrada
sPlaca.addEventListener('input', formatarPlaca);
sPreco.addEventListener('input', virgula);

btnSalvar.onclick = (e) => {
  e.preventDefault();

  // Verifica se todos os campos estão preenchidos
  if (sModelo.value === "" || sAno.value === "" || sPreco.value === "" || sCor.value === "" || sPlaca.value === "") {
    return;
  }

  // Criação do FormData para enviar dados em formato multipart/form-data
  const formData = new FormData();
  formData.append("modelo", sModelo.value);
  formData.append("ano", sAno.value);
  formData.append("preco", sPreco.value);
  formData.append("cor", sCor.value);
  formData.append("placa", sPlaca.value);

  // Verifica se uma imagem foi selecionada
  const imagemInput = document.querySelector("#m-imagem");  // O ID do campo de imagem no seu HTML
  if (imagemInput.files.length > 0) {
    formData.append("file", imagemInput.files[0]);  // Adiciona o arquivo
  }

  if (id) {
    // PUT: Atualizar carro existente
    fetch(`http://localhost:8080/carro/atualizar/${id}`, {
      method: 'PUT',
      body: formData,  // Não é necessário o header 'Content-Type', o fetch vai definir automaticamente
    })
    .then(response => response.json())
    .then(data => {
      console.log(data);
      itens = itens.map(item => item.id === id ? data : item);  // Atualiza a lista de itens
      renderTable();  // Re-renderiza a tabela
      modal.classList.remove("active");  // Fecha o modal
    })
    .catch(error => {
      console.error('Erro ao atualizar os dados:', error);
    });
  } else {
    // POST: Criar novo carro
    fetch('http://localhost:8080/carro/criar', {
      method: 'POST',
      body: formData,  // Envia o FormData com todos os dados
    })
    .then(response => response.json())
    .then(data => {
      console.log(data);
      itens.push(data);  // Adiciona o novo carro à lista
      renderTable();  // Re-renderiza a tabela
      modal.classList.remove("active");  // Fecha o modal
    })
    .catch(error => {
      console.error('Erro ao criar o carro:', error);
    });
  }
}; 

function deleteItem(index) {
  const idToDelete = itens[index].id;

  function deleteItem(index) {
    const idToDelete = itens[index].id;
  
    fetch(`http://localhost:8080/carro/delete/${idToDelete}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                itens.splice(index, 1);   
                renderTable();   
            } else {
                console.error('Erro ao excluir o carro');
            }
        })
        .catch(error => {
            console.error('Erro ao excluir os dados:', error);
        });
  }
}  