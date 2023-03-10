import Home from './pages/Home';
import './App.css';
import Sidebar from './components/Sidebar';
import {Routes, Route} from 'react-router-dom';
import CountryInfo from './pages/CountryInfo';
import OrderDetail from './pages/OrderDetail';
import Report from './pages/Report';
import SupplyDemandVaccine from './pages/SupplyDemandVaccine';
import Suggestions from './pages/Suggestions';

function App() {
	return (
		<div className='flex'>
			<Sidebar />
			<Routes>
				<Route path='/' element={<Home />} />
				<Route path='/country-info' element={<CountryInfo />} />
				<Route path='/order-detail' element={<OrderDetail />} />
				<Route path='/report' element={<Report />} />
				<Route path='/suggestions' element={<Suggestions />} />
				<Route path='/supply-demand-vaccine' element={<SupplyDemandVaccine />} />
			</Routes>
		</div>
	);
}

export default App;
