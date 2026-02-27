import React, { useState, useEffect } from 'react';
import { 
  Leaf, Droplets, Sun, Plus, LogOut, 
  ArrowLeft, Trash2, Settings, ShieldCheck, RefreshCw, X, MapPin, Edit3, Save, Calendar
} from 'lucide-react';

// Endereço base da API Spring Boot
const API_BASE_URL = "http://localhost:8080"; 

// --- ESTILOS GLOBAIS ---
if (typeof document !== 'undefined') {
  const criticalStyle = document.createElement('style');
  criticalStyle.id = 'critical-app-style';
  criticalStyle.innerHTML = `
    html, body, #root { 
      margin: 0 !important; 
      padding: 0 !important; 
      width: 100vw !important; 
      height: 100vh !important; 
      background-color: #f8fafc !important; 
      display: flex !important;
      flex-direction: column !important;
      font-family: system-ui, -apple-system, sans-serif !important;
    }
    input, textarea, select { color: #1e293b !important; }
    body { opacity: 0; transition: opacity 0.2s; }
    body.tailwind-ready { opacity: 1; }
  `;
  document.head.appendChild(criticalStyle);

  if (!document.getElementById('tailwind-cdn')) {
    const script = document.createElement('script');
    script.id = 'tailwind-cdn';
    script.src = "https://cdn.tailwindcss.com";
    script.onload = () => document.body.classList.add('tailwind-ready');
    document.head.appendChild(script);

    const config = document.createElement('script');
    config.innerHTML = `
      tailwind.config = {
        theme: {
          extend: {
            colors: {
              emerald: { 50: '#ecfdf5', 100: '#d1fae5', 200: '#a7f3d0', 500: '#10b981', 600: '#059669', 700: '#047857', 900: '#064e3b' }
            }
          }
        }
      }
    `;
    document.head.appendChild(config);
  } else {
    document.body.classList.add('tailwind-ready');
  }
}

const App = () => {
  const [currentPage, setCurrentPage] = useState('login'); 
  const [user, setUser] = useState(null); 
  const [plants, setPlants] = useState([]); 
  const [message, setMessage] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  
  const [showPlantModal, setShowPlantModal] = useState(false);
  const [editingPlant, setEditingPlant] = useState(null);

  // --- POLIMENTO: Título da Página e Ícone (Favicon) ---
  useEffect(() => {
    // Nome da página atualizado
    document.title = "CloudSeed";
    
    // Atualização do ícone para o emoji 🌱
    const link = document.querySelector("link[rel*='icon']") || document.createElement('link');
    link.type = 'image/x-icon';
    link.rel = 'shortcut icon';
    link.href = 'data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🌱</text></svg>';
    document.getElementsByTagName('head')[0].appendChild(link);
  }, []);

  const showMsg = (type, text) => {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 4000);
  };

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return "Bom dia";
    if (hour < 18) return "Boa tarde";
    return "Boa noite";
  };

  const fetchPlants = async (userId) => {
    if (!userId) return;
    try {
      const response = await fetch(`${API_BASE_URL}/plants`, {
        mode: 'cors',
        headers: { 'Accept': 'application/json' }
      });
      if (response.ok) {
        const allData = await response.json();
        if (Array.isArray(allData)) {
          const filtered = allData.filter(p => {
             const pUserId = p.userId || (p.user && p.user.id);
             return !pUserId || String(pUserId) === String(userId);
          });
          setPlants(filtered);
        } else {
          setPlants([]);
        }
      } else if (response.status === 204) {
        setPlants([]);
      }
    } catch (error) {
      console.error("Erro na busca de plantas:", error);
    }
  };

  useEffect(() => {
    if (user && user.id && currentPage === 'dashboard') {
      fetchPlants(user.id);
    }
  }, [user, currentPage]);

  const handleLogin = async (e) => {
    e.preventDefault();
    if (isLoading) return;
    setIsLoading(true);
    
    const loginData = {
      email: e.target.email.value.trim(),
      password: e.target.password.value
    };

    try {
      const response = await fetch(`${API_BASE_URL}/users/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginData)
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok) {
        setUser({
          ...data,
          username: data.username || data.name || 'Usuário'
        });
        setCurrentPage('dashboard');
        showMsg('success', `${getGreeting()}, ${data.username || 'de volta'}!`);
      } else {
        showMsg('error', data.message || 'E-mail ou palavra-passe incorretos.');
      }
    } catch (error) {
      showMsg('error', 'O servidor está offline ou inacessível.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    if (isLoading) return;
    setIsLoading(true);
    const registerData = {
      username: e.target.username.value.trim(),
      email: e.target.email.value.trim(),
      password: e.target.password.value
    };
    try {
      const response = await fetch(`${API_BASE_URL}/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registerData)
      });
      if (response.ok) {
        showMsg('success', 'Conta criada com sucesso!');
        setCurrentPage('login');
      } else {
        showMsg('error', 'Erro ao criar conta. Verifique os dados.');
      }
    } catch (error) {
      showMsg('error', 'Erro de ligação.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSavePlant = async (e) => {
    e.preventDefault();
    if (isLoading) return;
    setIsLoading(true);

    const formData = new FormData(e.target);
    const plantData = {
      name: formData.get('plantName'),
      species: formData.get('plantSpecies'),
      location: formData.get('plantLocation'),
      wateringFrequency: formData.get('wateringFrequency'),
      lastWateringDate: formData.get('lastWateringDate')
    };

    const url = editingPlant ? `${API_BASE_URL}/plants/${editingPlant.id}` : `${API_BASE_URL}/plants/user/${user.id}`;
    const method = editingPlant ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(plantData)
      });

      if (response.ok) {
        await fetchPlants(user.id);
        showMsg('success', editingPlant ? 'Informações atualizadas!' : 'Planta adicionada!');
        closeModals();
      } else {
        showMsg('error', 'Não foi possível salvar a planta.');
      }
    } catch (error) {
      showMsg('error', 'Erro de rede.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeletePlant = async (plantId) => {
    if (!window.confirm("Deseja mesmo remover esta planta?")) return;
    try {
      const response = await fetch(`${API_BASE_URL}/plants/${plantId}`, { method: 'DELETE' });
      if (response.ok) {
        setPlants(prev => prev.filter(p => p.id !== plantId));
        showMsg('success', 'Planta removida.');
      }
    } catch (error) {
      showMsg('error', 'Erro ao eliminar.');
    }
  };

  const closeModals = () => {
    setShowPlantModal(false);
    setEditingPlant(null);
  };

  const MessageToast = () => message && (
    <div className={`fixed top-8 left-1/2 -translate-x-1/2 z-[100] px-8 py-4 rounded-[2rem] text-white font-black shadow-2xl animate-in fade-in slide-in-from-top-8 duration-300 flex items-center gap-4 border border-white/10 ${message.type === 'success' ? 'bg-emerald-600 shadow-emerald-200/50' : 'bg-rose-500 shadow-rose-200/50'}`}>
      {message.type === 'success' ? <ShieldCheck size={24}/> : <X size={24}/>}
      <span className="text-sm tracking-tight">{message.text}</span>
    </div>
  );

  const PlantModal = () => showPlantModal && (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-50 flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-md rounded-[3rem] p-10 shadow-2xl overflow-y-auto max-h-[90vh] border border-white/20 animate-in zoom-in-95">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl font-black text-slate-800 tracking-tight">{editingPlant ? 'Editar Planta' : 'Nova Planta'}</h2>
          <button onClick={closeModals} className="p-2 bg-slate-50 rounded-full text-slate-400 hover:text-slate-600 transition-colors"><X size={20}/></button>
        </div>
        <form onSubmit={handleSavePlant} className="space-y-5">
          <div className="space-y-1.5">
            <label className="text-[10px] font-black uppercase text-slate-400 ml-2 tracking-widest">Identificação</label>
            <input name="plantName" defaultValue={editingPlant?.name || ''} required placeholder="Nome da Planta" className="w-full px-6 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-500/10 transition-all" />
          </div>
          <input name="plantSpecies" defaultValue={editingPlant?.species || ''} placeholder="Espécie (ex: Lavanda)" className="w-full px-6 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-500/10 transition-all" />
          <input name="plantLocation" defaultValue={editingPlant?.location || ''} placeholder="Onde ela mora? (ex: Sacada)" className="w-full px-6 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-500/10 transition-all" />
          
          <div className="p-6 bg-emerald-50 rounded-[2.5rem] border border-emerald-100 space-y-4">
            <div className="flex items-center gap-2 text-emerald-700 font-black text-xs uppercase tracking-wider">
               <Droplets size={16} /> <span>Plano de Rega</span>
            </div>
            <select name="wateringFrequency" defaultValue={editingPlant?.schedule?.frequency || 'WEEKLY'} className="w-full px-4 py-3 bg-white border border-emerald-200 rounded-xl outline-none text-sm font-bold text-slate-700">
              <option value="DAILY">Todos os dias</option>
              <option value="TWICE_A_WEEK">2x por semana</option>
              <option value="WEEKLY">Semanalmente</option>
              <option value="BIWEEKLY">Quinzenalmente</option>
              <option value="MONTHLY">Mensalmente</option>
            </select>
            <input name="lastWateringDate" type="date" defaultValue={editingPlant?.schedule?.lastWateringDate || new Date().toISOString().split('T')[0]} className="w-full px-4 py-3 bg-white border border-emerald-200 rounded-xl outline-none text-sm font-bold text-slate-700" />
          </div>
          <button disabled={isLoading} type="submit" className="w-full bg-emerald-600 text-white font-black py-5 rounded-[2rem] shadow-xl hover:bg-emerald-700 transition-all disabled:opacity-50 flex items-center justify-center gap-2">
            {isLoading ? <RefreshCw className="animate-spin" /> : <Save size={20} />}
            {editingPlant ? 'Guardar Alterações' : 'Plantar Agora'}
          </button>
        </form>
      </div>
    </div>
  );

  if (currentPage === 'login') {
    return (
      <div className="flex-1 flex items-center justify-center p-6 bg-emerald-50/40">
        <MessageToast />
        <div className="w-full max-w-md bg-white rounded-[4rem] shadow-2xl p-12 flex flex-col items-center border border-white/60">
          <div className="bg-emerald-600 p-6 rounded-[2.2rem] mb-8 shadow-2xl shadow-emerald-200 animate-in zoom-in-50 duration-500">
            <Leaf className="text-white w-12 h-12" />
          </div>
          <h1 className="text-4xl font-black text-slate-800 mb-2 tracking-tighter uppercase italic">CloudSeed</h1>
          <p className="text-slate-400 font-black text-[10px] mb-12 tracking-[0.3em] uppercase opacity-70">Digital Gardening</p>
          <form onSubmit={handleLogin} className="w-full space-y-4">
            <div className="group relative">
               <input name="email" type="email" required placeholder="E-mail" className="w-full px-6 py-5 bg-slate-50 border border-slate-100 rounded-[1.5rem] outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 transition-all font-medium" />
            </div>
            <input name="password" type="password" required placeholder="Palavra-passe" className="w-full px-6 py-5 bg-slate-50 border border-slate-100 rounded-[1.5rem] outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 transition-all font-medium" />
            <button disabled={isLoading} className="w-full bg-emerald-600 text-white font-black py-5 rounded-[1.5rem] shadow-xl shadow-emerald-100 hover:bg-emerald-700 transition-all active:scale-95 flex justify-center items-center gap-3">
              {isLoading ? <RefreshCw className="animate-spin" /> : 'Entrar no Jardim'}
            </button>
          </form>
          <button onClick={() => setCurrentPage('register')} className="mt-12 text-emerald-600 font-black text-[11px] uppercase tracking-widest hover:text-emerald-800 transition-colors">Ainda não tens conta? Regista-te</button>
        </div>
      </div>
    );
  }

  if (currentPage === 'register') {
    return (
      <div className="flex-1 flex items-center justify-center p-6 bg-slate-50">
        <MessageToast />
        <div className="w-full max-w-md bg-white rounded-[3rem] shadow-2xl p-12 border border-slate-100">
          <button onClick={() => setCurrentPage('login')} className="mb-10 p-3 text-slate-400 hover:text-emerald-600 bg-slate-50 rounded-full transition-all"><ArrowLeft /></button>
          <h2 className="text-3xl font-black text-slate-800 mb-3 tracking-tight">Cria a tua conta</h2>
          <p className="text-slate-400 mb-10 font-bold text-sm">O primeiro passo para o teu jardim perfeito.</p>
          <form onSubmit={handleRegister} className="space-y-4">
            <input name="username" required placeholder="Como te chamas?" className="w-full px-6 py-5 bg-slate-50 border border-slate-100 rounded-2xl outline-none focus:border-emerald-500 font-medium" />
            <input name="email" type="email" required placeholder="E-mail" className="w-full px-6 py-5 bg-slate-50 border border-slate-100 rounded-2xl outline-none focus:border-emerald-500 font-medium" />
            <input name="password" type="password" required placeholder="Cria uma palavra-passe" className="w-full px-6 py-5 bg-slate-50 border border-slate-100 rounded-2xl outline-none focus:border-emerald-500 font-medium" />
            <button disabled={isLoading} className="w-full bg-slate-800 text-white font-black py-5 rounded-2xl shadow-xl mt-6 flex justify-center items-center gap-2 tracking-wide uppercase text-sm">
               {isLoading ? <RefreshCw className="animate-spin" /> : 'Concluir Registo'}
            </button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col bg-[#fcfdfc]">
      <MessageToast />
      <PlantModal />
      <header className="bg-white/90 backdrop-blur-md border-b px-8 py-5 flex justify-between items-center sticky top-0 z-10">
        <div className="flex items-center gap-3">
          <div className="bg-emerald-600 p-2.5 rounded-2xl text-white shadow-lg shadow-emerald-100"><Leaf size={22} /></div>
          <span className="text-2xl font-black text-slate-800 tracking-tighter uppercase italic">CloudSeed</span>
        </div>
        <button onClick={() => { setUser(null); setPlants([]); setCurrentPage('login'); }} className="p-3 text-slate-400 hover:text-rose-500 hover:bg-rose-50 rounded-2xl transition-all">
          <LogOut size={22} />
        </button>
      </header>

      <main className="flex-1 p-8 max-w-6xl mx-auto w-full">
        <div className="bg-gradient-to-br from-emerald-800 to-emerald-950 rounded-[3.5rem] p-12 text-white relative overflow-hidden mb-12 shadow-2xl shadow-emerald-200/40">
          <div className="relative z-10">
            {/* Rótulo alterado de Dashboard para Jardim Virtual */}
            <div className="inline-block px-5 py-1.5 bg-emerald-500/20 backdrop-blur-md rounded-full text-[9px] font-black uppercase tracking-[0.3em] mb-6 text-emerald-300 border border-emerald-400/20">Jardim Virtual</div>
            <h2 className="text-4xl font-black mb-4 tracking-tighter">{getGreeting()}, {user?.username}!</h2>
            <p className="text-emerald-100/70 font-bold max-w-md leading-relaxed text-sm">
              {new Date().getHours() < 18 ? "Tenha um bom dia hoje." : "Tenha uma noite tranquila."} 
              Tens {plants.length} {plants.length === 1 ? 'espécime' : 'espécimes'} a crescer sob a tua supervisão.
            </p>
          </div>
          <Leaf className="absolute -right-16 -bottom-16 w-80 h-80 text-emerald-700/20 rotate-12" />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 pb-20">
          <div onClick={() => { setEditingPlant(null); setShowPlantModal(true); }} className="bg-white p-10 rounded-[3.5rem] border-4 border-dashed border-slate-100 flex flex-col items-center justify-center text-center group hover:border-emerald-400 hover:bg-emerald-50/20 transition-all cursor-pointer h-full min-h-[300px] shadow-sm">
            <div className="bg-slate-50 p-7 rounded-full group-hover:bg-emerald-100 group-hover:scale-110 transition-all mb-6">
                <Plus className="text-slate-300 group-hover:text-emerald-600" size={48} />
            </div>
            <p className="font-black text-slate-300 group-hover:text-emerald-700 uppercase text-[11px] tracking-[0.25em]">Nova Planta</p>
          </div>

          {plants.map((plant) => (
            <div key={plant.id} className="bg-white p-10 rounded-[3.5rem] shadow-sm relative group hover:shadow-2xl hover:-translate-y-3 transition-all border border-slate-50 flex flex-col animate-in fade-in slide-in-from-bottom-4">
              <div className="flex justify-between items-start mb-8">
                <div className="bg-emerald-100/50 p-5 rounded-[2rem] text-emerald-700 group-hover:bg-emerald-600 group-hover:text-white transition-all shadow-sm"><Leaf size={28} /></div>
                <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all translate-x-2 group-hover:translate-x-0">
                  <button onClick={() => { setEditingPlant(plant); setShowPlantModal(true); }} className="p-3 bg-slate-50 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-2xl transition-all"><Edit3 size={18} /></button>
                  <button onClick={() => handleDeletePlant(plant.id)} className="p-3 bg-slate-50 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-2xl transition-all"><Trash2 size={18} /></button>
                </div>
              </div>
              <h3 className="text-2xl font-black text-slate-800 mb-1 truncate tracking-tight">{plant.name}</h3>
              <p className="text-[10px] font-black text-emerald-600/50 uppercase tracking-[0.2em] mb-8">{plant.species || 'Planta Doméstica'}</p>
              
              <div className="space-y-4 mb-10 flex-1">
                <div className="flex items-center gap-3 text-slate-500 bg-slate-50/80 p-4 rounded-[1.5rem]">
                  <MapPin size={16} className="text-emerald-500" />
                  <span className="text-xs font-bold">{plant.location || 'Local a definir'}</span>
                </div>
                <div className="flex items-center gap-3 text-slate-500 bg-slate-50/80 p-4 rounded-[1.5rem]">
                  <Calendar size={16} className="text-emerald-500" />
                  <span className="text-xs font-bold">Rega: {plant.schedule?.lastWateringDate || 'Pendente'}</span>
                </div>
              </div>

              <div className="pt-8 border-t border-slate-50 flex justify-between items-center">
                 <div className="flex flex-col">
                    <span className="text-[9px] font-black text-slate-300 uppercase tracking-widest">Rotina</span>
                    <span className="text-sm font-black text-slate-700 tracking-tight">{plant.schedule?.frequency || 'Variável'}</span>
                 </div>
                 <div className="bg-emerald-500 text-white px-5 py-2 rounded-2xl text-[9px] font-black uppercase tracking-widest shadow-lg shadow-emerald-200">Ativa</div>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default App;