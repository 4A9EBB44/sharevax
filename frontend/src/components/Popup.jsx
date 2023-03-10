const Popup = ({countryName, vaccinationRate, vaccineConsumption, vaccineProduction, vaccineStock}) => {
	return (
		<div>
			<div className='font-semibold'>
				Country: <span className='font-normal'>{countryName}</span>
			</div>
			<div className='font-semibold'>
				Vaccination rate: <span className='font-normal'>{vaccinationRate}</span>
			</div>
			<div className='font-semibold'>
				Daily vaccine consumption: <span className='font-normal'>{vaccineConsumption}</span>
			</div>
			<div className='font-semibold'>
				Daily vaccine production: <span className='font-normal'>{vaccineProduction}</span>
			</div>
			<div className='font-semibold'>
				Vaccine stock: <span className='font-normal'>{vaccineStock}</span>
			</div>
		</div>
	);
};

export default Popup;
